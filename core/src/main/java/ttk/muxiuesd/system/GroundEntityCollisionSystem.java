package ttk.muxiuesd.system;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.chunk.Chunk;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.wall.Wall;

/**
 * 实体碰撞系统
 * <p>
 * 实体与实体之间的碰撞，实体与墙体的碰撞
 * */
public class GroundEntityCollisionSystem extends WorldSystem {
    public final String TAG = this.getClass().getName();

    // 精度控制：确保分离后无重叠且不产生新碰撞
    private static final float EPS = 0.00001f;
    // 最大碰撞修正次数：防止极端情况下的死循环
    private static final int MAX_FIXES = 5;

    private final EntitySystem es;
    private final ChunkSystem cs;

    public GroundEntityCollisionSystem(World world) {
        super(world);
        this.es = getWorld().getSystem(EntitySystem.class);
        this.cs = getWorld().getSystem(ChunkSystem.class);
    }

    @Override
    public void update(float delta) {
        Player player = es.getPlayer();
        if (player == null) return;

        Rectangle hitbox = player.getHitbox();
        Vector2 vel = player.getVelocity();
        // 计算本次移动距离
        float moveX = vel.x * delta;
        float moveY = vel.y * delta;

        // 保存原始位置用于修正
        float originalX = hitbox.x;
        float originalY = hitbox.y;

        // 1. 先处理X轴移动
        if (Math.abs(moveX) > EPS) {
            hitbox.x += moveX;
            // 检测并修正X轴碰撞
            fixCollisions(hitbox, 1, 0, moveX);
        }

        // 2. 再处理Y轴移动（基于X轴修正后的位置）
        if (Math.abs(moveY) > EPS) {
            hitbox.y += moveY;
            // 检测并修正Y轴碰撞
            fixCollisions(hitbox, 0, 1, moveY);
        }

        // 更新玩家位置
        player.setPosition(hitbox.x - Player.HITBOX_OFFSET.x, hitbox.y - Player.HITBOX_OFFSET.y);
        // 重置速度（根据需求可保留非碰撞轴速度）
        player.velX = 0;
        player.velY = 0;
    }

    /**
     * 修正指定轴上的碰撞
     * @param hitbox 玩家碰撞箱
     * @param axisX 1表示X轴，0表示非X轴
     * @param axisY 1表示Y轴，0表示非Y轴
     * @param move 该轴上的移动距离
     */
    private void fixCollisions(Rectangle hitbox, int axisX, int axisY, float move) {
        // 收集当前碰撞的墙体
        Array<Wall<?>> collidingWalls = getCollidingWalls(hitbox);
        int fixes = 0;

        // 多次修正直到无碰撞或达到最大次数
        while (!collidingWalls.isEmpty() && fixes < MAX_FIXES) {
            // 计算需要的总分离量
            float totalSeparation = 0;
            for (Wall<?> wall : collidingWalls) {
                Rectangle wallBox = wall.getHitbox();
                if (hitbox.overlaps(wallBox)) {
                    // 计算当前墙体在移动方向上的重叠量
                    float overlap = calculateOverlap(hitbox, wallBox, axisX, axisY, move);
                    totalSeparation += overlap;
                }
            }

            // 应用分离（向移动反方向）
            if (Math.abs(totalSeparation) > EPS) {
                // 取平均分离量，避免多墙体叠加导致过度分离
                float avgSeparation = totalSeparation / collidingWalls.size;
                // 确保分离方向与移动方向相反
                float separation = (move > 0) ? -avgSeparation : avgSeparation;
                // 添加精度补偿，彻底离开墙体
                separation += (separation > 0) ? EPS : -EPS;

                hitbox.x += axisX * separation;
                hitbox.y += axisY * separation;
            }

            // 重新检测碰撞
            collidingWalls = getCollidingWalls(hitbox);
            fixes++;
        }
    }

    /**
     * 计算指定轴上的重叠量（仅在移动方向上有效）
     */
    private float calculateOverlap(Rectangle player, Rectangle wall, int axisX, int axisY, float move) {
        if (axisX == 1) {
            // X轴重叠计算
            float playerRight = player.x + player.width;
            float wallRight = wall.x + wall.width;

            // 向右移动时的重叠（玩家右侧撞墙左侧）
            if (move > 0) {
                return Math.max(0, playerRight - wall.x);
            }
            // 向左移动时的重叠（玩家左侧撞墙右侧）
            else {
                return Math.max(0, wallRight - player.x);
            }
        } else {
            // Y轴重叠计算
            float playerTop = player.y + player.height;
            float wallTop = wall.y + wall.height;

            // 向上移动时的重叠（玩家顶部撞墙底部）
            if (move > 0) {
                return Math.max(0, playerTop - wall.y);
            }
            // 向下移动时的重叠（玩家底部撞墙顶部）
            else {
                return Math.max(0, wallTop - player.y);
            }
        }
    }

    /**
     * 收集与玩家碰撞箱重叠的墙体（仅检测必要区块）
     */
    private Array<Wall<?>> getCollidingWalls(Rectangle hitbox) {
        Array<Wall<?>> result = new Array<>();

        // 计算玩家所在区块及相邻区块
        ChunkPosition pos = cs.getChunkPosition(
            hitbox.x + hitbox.width / 2,
            hitbox.y + hitbox.height / 2
        );

        // 检测3x3区块范围（当前区块+周围8个）
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                Chunk chunk = cs.getChunk(pos.getX() + dx, pos.getY() + dy);
                if (chunk == null) continue;

                // 遍历区块内所有墙体，检测碰撞
                chunk.traversal((x, y) -> {
                    Wall<?> wall = chunk.getWall(x, y);
                    if (wall != null && hitbox.overlaps(wall.getHitbox())) {
                        result.add(wall);
                    }
                });
            }
        }
        return result;
    }
}

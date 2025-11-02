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


    // 精度控制
    private static final float EPS = 0.0001f;
    // 最大碰撞修正次数
    private static final int MAX_FIXES = 5;
    // 连续碰撞检测的最大步长（越小越精确但性能消耗略高）
    // 建议设置为墙体最小单位的1/2（这里假设墙体最小单位是1x1）
    private static final float MAX_STEP = 0.5f;

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

        // 计算总移动距离
        float totalMoveX = vel.x * delta;
        float totalMoveY = vel.y * delta;

        // 计算需要分解的步数（解决高速移动隧穿问题的核心）
        float totalDistance = (float) Math.sqrt(totalMoveX * totalMoveX + totalMoveY * totalMoveY);
        int steps = (int) Math.ceil(totalDistance / MAX_STEP);
        if (steps == 0) steps = 1; // 至少一步

        // 每步的移动量
        float stepX = totalMoveX / steps;
        float stepY = totalMoveY / steps;

        // 分步移动并检测碰撞
        for (int i = 0; i < steps; i++) {
            // X轴分步移动
            if (Math.abs(stepX) > EPS) {
                hitbox.x += stepX;
                if (fixCollisions(hitbox, 1, 0, stepX)) {
                    // 如果发生碰撞，剩余步数不再移动X轴
                    stepX = 0;
                }
            }

            // Y轴分步移动
            if (Math.abs(stepY) > EPS) {
                hitbox.y += stepY;
                if (fixCollisions(hitbox, 0, 1, stepY)) {
                    // 如果发生碰撞，剩余步数不再移动Y轴
                    stepY = 0;
                }
            }

            // 如果X和Y轴都发生碰撞，提前退出循环
            if (Math.abs(stepX) < EPS && Math.abs(stepY) < EPS) {
                break;
            }
        }

        // 更新玩家位置
        player.setPosition(
            hitbox.x - Player.HITBOX_OFFSET.x,
            hitbox.y - Player.HITBOX_OFFSET.y
        );
        // 重置速度
        player.velX = 0;
        player.velY = 0;
    }

    /**
     * 修正碰撞并返回是否发生了碰撞
     */
    private boolean fixCollisions(Rectangle hitbox, int axisX, int axisY, float move) {
        Array<Wall<?>> collidingWalls = getCollidingWalls(hitbox);
        if (collidingWalls.isEmpty()) {
            return false; // 无碰撞
        }

        int fixes = 0;
        boolean collided = false;

        while (!collidingWalls.isEmpty() && fixes < MAX_FIXES) {
            float totalSeparation = 0;
            for (Wall<?> wall : collidingWalls) {
                Rectangle wallBox = wall.getHitbox();
                if (hitbox.overlaps(wallBox)) {
                    float overlap = calculateOverlap(hitbox, wallBox, axisX, axisY, move);
                    totalSeparation += overlap;
                    collided = true;
                }
            }

            if (Math.abs(totalSeparation) > EPS) {
                float avgSeparation = totalSeparation / collidingWalls.size;
                float separation = (move > 0) ? -avgSeparation : avgSeparation;
                separation += (separation > 0) ? EPS : -EPS;

                hitbox.x += axisX * separation;
                hitbox.y += axisY * separation;
            }

            collidingWalls = getCollidingWalls(hitbox);
            fixes++;
        }

        return collided;
    }

    /**
     * 计算精确的重叠量
     */
    private float calculateOverlap(Rectangle player, Rectangle wall, int axisX, int axisY, float move) {
        if (axisX == 1) {
            // X轴碰撞计算
            float playerRight = player.x + player.width;
            float wallLeft = wall.x;
            float wallRight = wall.x + wall.width;

            if (move > 0) {
                // 向右移动：玩家右侧与墙体左侧重叠
                return Math.max(0, playerRight - wallLeft);
            } else {
                // 向左移动：玩家左侧与墙体右侧重叠
                return Math.max(0, wallRight - player.x);
            }
        } else {
            // Y轴碰撞计算
            float playerTop = player.y + player.height;
            float wallBottom = wall.y;
            float wallTop = wall.y + wall.height;

            if (move > 0) {
                // 向上移动：玩家顶部与墙体底部重叠
                return Math.max(0, playerTop - wallBottom);
            } else {
                // 向下移动：玩家底部与墙体顶部重叠
                return Math.max(0, wallTop - player.y);
            }
        }
    }

    /**
     * 优化墙体检测范围，只检测移动路径上可能接触的区块
     */
    private Array<Wall<?>> getCollidingWalls(Rectangle hitbox) {
        Array<Wall<?>> result = new Array<>();

        // 计算玩家碰撞箱四角所在的区块，确保覆盖所有可能接触的区块
        int[] xChecks = {
            (int) hitbox.x,
            (int) (hitbox.x + hitbox.width)
        };
        int[] yChecks = {
            (int) hitbox.y,
            (int) (hitbox.y + hitbox.height)
        };

        // 收集所有需要检测的区块坐标
        Array<ChunkPosition> positions = new Array<>();
        for (int x : xChecks) {
            for (int y : yChecks) {
                ChunkPosition pos = cs.getChunkPosition(x, y);
                if (!positions.contains(pos, false)) {
                    positions.add(pos);
                }
            }
        }

        // 检测这些区块内的墙体
        for (ChunkPosition pos : positions) {
            // 检测当前区块及相邻区块（3x3范围）
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    Chunk chunk = cs.getChunk(pos.getX() + dx, pos.getY() + dy);
                    if (chunk == null) continue;

                    chunk.traversal((x, y) -> {
                        Wall<?> wall = chunk.getWall(x, y);
                        if (wall != null && wall.getHitbox() != null &&
                            hitbox.overlaps(wall.getHitbox())) {
                            result.add(wall);
                        }
                    });
                }
            }
        }

        return result;
    }
}

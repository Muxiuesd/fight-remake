package ttk.muxiuesd.system;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.registry.EntityTypes;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.chunk.Chunk;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.player.Player;
import ttk.muxiuesd.world.hitbox.Hitbox;
import ttk.muxiuesd.world.hitbox.RectHitbox;
import ttk.muxiuesd.world.wall.Wall;

/**
 * 地面的实体的碰撞系统
 * <p>
 * 处理所有地面实体与墙体的碰撞
 * */
public class GroundEntityCollisionSystem extends WorldSystem {
    public final String TAG = this.getClass().getName();

    // 精度控制
    private static final float EPS = 0.0001f;
    // 最大碰撞修正次数
    private static final int MAX_FIXES = 5;
    // 连续碰撞检测的最大步长（越小越精确但性能消耗略高）
    private static final float MAX_STEP = 0.5f;

    private final EntitySystem es;
    private final ChunkSystem cs;

    public GroundEntityCollisionSystem(World world) {
        super(world);
        this.es = getWorld().getSystem(EntitySystem.class);
        this.cs = getWorld().getSystem(ChunkSystem.class);
    }

    @Override
    public void update (float delta) {
        // 敌方实体与墙体的碰撞
        Array<Enemy<?>> enemies = es.getEnemyEntity();
        for (Enemy<?> enemy : enemies) {
            this.checkEntityWithWallCollisions(enemy, delta);
        }

        // 生物实体与墙体的碰撞
        Array<LivingEntity<?>> creatures = es.getEntityArray(EntityTypes.CREATURE);
        for (LivingEntity<?> creature : creatures) {
            this.checkEntityWithWallCollisions(creature, delta);
        }

        // 物品实体与墙体的碰撞
        Array<ItemEntity> items = es.getEntityArray(EntityTypes.ITEM_ENTITY);
        for (ItemEntity item : items) {
            this.checkEntityWithWallCollisions(item, delta);
        }

        // 玩家实体与墙体的碰撞
        Player player = es.getPlayer();
        if (player != null) {
            this.checkEntityWithWallCollisions(player, delta);
        }
    }

    /**
     * 核心算法：对某个实体的碰撞箱与墙体做连续碰撞检测 (CCD)
     * */
    public void checkEntityWithWallCollisions (Entity<?> entity, float delta) {
        Hitbox bodyHitbox = entity.getBodyHitbox();
        if (!(bodyHitbox instanceof RectHitbox rectBodyHitbox)) {
            return;
        }

        Vector2 vel = entity.getVelocity();
        // 速度为零的实体跳过（无移动无需碰撞检测）
        if (Math.abs(vel.x) < EPS && Math.abs(vel.y) < EPS) {
            return;
        }

        Rectangle rect = rectBodyHitbox.getRectangle();
        Vector2 rectBodyHitboxCenterPos = rectBodyHitbox.getCenterPos();
        // 计算碰撞箱左下角坐标与碰撞箱中心坐标的偏移量
        Vector2 rectPosToCenterDelta = new Vector2(
            rectBodyHitboxCenterPos.x - rect.x,
            rectBodyHitboxCenterPos.y - rect.y
        );
        // 计算碰撞箱中心坐标与实体坐标的偏移量
        Vector2 entityCenterPos = entity.getPosition();
        Vector2 entityCenterToRectCenterDelta = new Vector2(
            rectBodyHitboxCenterPos.x - entityCenterPos.x,
            rectBodyHitboxCenterPos.y - entityCenterPos.y
        );

        // 计算总移动距离
        float totalMoveX = vel.x * delta;
        float totalMoveY = vel.y * delta;

        // 计算需要分解的步数（解决高速移动隧穿问题）
        float totalDistance = (float) Math.sqrt(totalMoveX * totalMoveX + totalMoveY * totalMoveY);
        int steps = (int) Math.ceil(totalDistance / MAX_STEP);
        if (steps == 0) steps = 1;

        // 每步的移动量
        float stepX = totalMoveX / steps;
        float stepY = totalMoveY / steps;

        // 分步移动并检测碰撞
        for (int i = 0; i < steps; i++) {
            // X轴分步移动
            if (Math.abs(stepX) > EPS) {
                rect.x += stepX;
                if (this.fixCollisions(rect, 1, 0, stepX)) {
                    stepX = 0;
                    entity.setVelX(0);
                }
            }
            // Y轴分步移动
            if (Math.abs(stepY) > EPS) {
                rect.y += stepY;
                if (this.fixCollisions(rect, 0, 1, stepY)) {
                    stepY = 0;
                    entity.setVelY(0);
                }
            }
            // 如果X和Y轴都发生碰撞，提前退出循环
            if (Math.abs(stepX) < EPS && Math.abs(stepY) < EPS) {
                break;
            }
        }

        // 更新实体位置
        entity.setPosition(
            rect.x + rectPosToCenterDelta.x - entityCenterToRectCenterDelta.x,
            rect.y + rectPosToCenterDelta.y - entityCenterToRectCenterDelta.y
        );
    }

    /**
     * 修正碰撞并返回是否发生了碰撞
     */
    private boolean fixCollisions (Rectangle hitbox, int axisX, int axisY, float move) {
        Array<Wall<?>> collidingWalls = this.getCollidingWalls(hitbox);
        if (collidingWalls.isEmpty()) {
            return false;
        }

        int fixes = 0;
        boolean collided = false;

        while (!collidingWalls.isEmpty() && fixes < MAX_FIXES) {
            float totalSeparation = 0;
            for (Wall<?> wall : collidingWalls) {
                Rectangle wallBox = wall.getHitboxRectangle();
                if (hitbox.overlaps(wallBox)) {
                    float overlap = this.calculateOverlap(hitbox, wallBox, axisX, axisY, move);
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

            collidingWalls = this.getCollidingWalls(hitbox);
            fixes++;
        }

        return collided;
    }

    /**
     * 计算精确的重叠量
     */
    private float calculateOverlap (Rectangle rect, Rectangle wall, int axisX, int axisY, float move) {
        if (axisX == 1) {
            float rectRight = rect.x + rect.width;
            float wallLeft = wall.x;
            float wallRight = wall.x + wall.width;

            if (move > 0) {
                return Math.max(0, rectRight - wallLeft);
            } else {
                return Math.max(0, wallRight - rect.x);
            }
        } else {
            float rectTop = rect.y + rect.height;
            float wallBottom = wall.y;
            float wallTop = wall.y + wall.height;

            if (move > 0) {
                return Math.max(0, rectTop - wallBottom);
            } else {
                return Math.max(0, wallTop - rect.y);
            }
        }
    }

    /**
     * 优化墙体检测范围，只检测碰撞箱所在的区块
     */
    private Array<Wall<?>> getCollidingWalls (Rectangle hitbox) {
        Array<Wall<?>> result = new Array<>();

        int[] xChecks = {
            (int) Math.floor(hitbox.x),
            (int) Math.floor(hitbox.x + hitbox.width)
        };
        int[] yChecks = {
            (int) Math.floor(hitbox.y),
            (int) Math.floor(hitbox.y + hitbox.height)
        };

        Array<ChunkPosition> positions = new Array<>();
        for (int x : xChecks) {
            for (int y : yChecks) {
                ChunkPosition pos = cs.getChunkPos(x, y);
                if (!positions.contains(pos, false)) {
                    positions.add(pos);
                }
            }
        }

        for (ChunkPosition pos : positions) {
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    Chunk chunk = cs.getChunk(pos.getX() + dx, pos.getY() + dy);
                    if (chunk == null) continue;

                    chunk.traversal((x, y) -> {
                        Wall<?> wall = chunk.getWall(x, y);
                        if (wall != null && wall.getHitboxRectangle() != null &&
                            hitbox.overlaps(wall.getHitboxRectangle())) {
                            result.add(wall);
                        }
                    });
                }
            }
        }

        return result;
    }
}

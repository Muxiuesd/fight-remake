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

import java.util.HashMap;
import java.util.Map;

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

    /// 实体间软碰撞参数
    //两个轴的重叠量都小于该值时不施加推开力（允许轻微贴脸重叠，如近战贴身）
    private static final float MIN_PUSH_OVERLAP = 0.1f;
    //推开力系数：重叠每多 1 格，推开速度增加该值（格/秒）
    private static final float PUSH_STIFFNESS = 4f;
    //推开速度上限（格/秒），防止推开力超过移动意图导致实体永远无法靠近
    private static final float MAX_PUSH_SPEED = 2f;

    private final EntitySystem es;
    private final ChunkSystem cs;
    /// 实体间推挤的参与者复用数组（避免每帧分配）
    private final Array<Entity<?>> pushParticipants = new Array<>();
    /// 本帧每个实体收到的推开速度分量（位移后清零，保证分离后无排斥残留）
    private final HashMap<Entity<?>, Vector2> pushVelocities = new HashMap<>();

    public GroundEntityCollisionSystem(World world) {
        super(world);
        this.es = getWorld().getSystem(EntitySystem.class);
        this.cs = getWorld().getSystem(ChunkSystem.class);
    }

    @Override
    public void update (float delta) {
        // 实体间软碰撞：重叠时向最小分离轴方向叠加推开速度（在位移之前生效，
        // 推开速度参与本帧位移；位移完成后立即清零，分离后不再有任何排斥作用）
        this.applyPushForces();

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

        // 推开速度用完即清零：不影响下一帧的移动意图（尤其玩家的启动加速 lerp，
        // 否则推开分量会以 lerp 比例残留滑行，分离后仍有"排斥"感）
        this.clearPushForces();
    }

    /**
     * 实体间软碰撞：hitbox 允许重叠，但重叠时会产生一股推开"力"（叠加到速度矢量）
     * <p>
     * 参与推挤的实体：敌人、生物、玩家（物品小而多，不参与，避免抖动）。
     * 推开速度沿最小分离轴方向施加，大小与重叠量成正比（弹性推开），上限为
     * {@link #MAX_PUSH_SPEED}。推开速度只在施加的当帧位移中生效，位移后由
     * {@link #clearPushForces()} 清零，表现为"每帧重新施加的弹性力"而非速度累积
     */
    private void applyPushForces () {
        Array<Entity<?>> participants = this.pushParticipants;
        participants.clear();
        pushVelocities.clear();
        for (Enemy<?> enemy : es.getEnemyEntity()) {
            participants.add(enemy);
        }
        for (LivingEntity<?> creature : es.getEntityArray(EntityTypes.CREATURE)) {
            participants.add(creature);
        }
        Player player = es.getPlayer();
        if (player != null) {
            participants.add(player);
        }

        for (int i = 0; i < participants.size; i++) {
            Entity<?> a = participants.get(i);
            for (int j = i + 1; j < participants.size; j++) {
                Entity<?> b = participants.get(j);
                //快速距离检查（实体碰撞箱默认以中心 ±宽高一半，用实体宽高近似）
                float dx = a.getX() - b.getX();
                float dy = a.getY() - b.getY();
                float halfSumX = (a.getWidth() + b.getWidth()) / 2f;
                float halfSumY = (a.getHeight() + b.getHeight()) / 2f;
                if (Math.abs(dx) >= halfSumX || Math.abs(dy) >= halfSumY) {
                    continue;
                }

                //最小分离轴：重叠量小的轴（重叠方向与移动方向垂直时可沿墙分离）
                float overlapX = halfSumX - Math.abs(dx);
                float overlapY = halfSumY - Math.abs(dy);
                //轻微贴脸重叠不推（两轴重叠量都很小时允许贴身）
                if (overlapX < MIN_PUSH_OVERLAP && overlapY < MIN_PUSH_OVERLAP) {
                    continue;
                }

                if (overlapX < overlapY) {
                    //沿 X 轴推开，力与重叠量成正比且封顶
                    float push = Math.min(overlapX * PUSH_STIFFNESS, MAX_PUSH_SPEED);
                    float sign = dx > 0 ? 1f : -1f;
                    a.setVelX(a.getVelX() + sign * push);
                    b.setVelX(b.getVelX() - sign * push);
                    //记录推开分量，位移后清零
                    this.accumulatePushVelocity(a, sign * push, 0);
                    this.accumulatePushVelocity(b, -sign * push, 0);
                } else {
                    //沿 Y 轴推开
                    float push = Math.min(overlapY * PUSH_STIFFNESS, MAX_PUSH_SPEED);
                    float sign = dy > 0 ? 1f : -1f;
                    a.setVelY(a.getVelY() + sign * push);
                    b.setVelY(b.getVelY() - sign * push);
                    //记录推开分量，位移后清零
                    this.accumulatePushVelocity(a, 0, sign * push);
                    this.accumulatePushVelocity(b, 0, -sign * push);
                }
            }
        }
    }

    /**
     * 累加记录某个实体的推开速度分量（一个实体可能同时被多个实体推）
     */
    private void accumulatePushVelocity (Entity<?> entity, float pushX, float pushY) {
        Vector2 pushVel = this.pushVelocities.get(entity);
        if (pushVel == null) {
            pushVel = new Vector2();
            this.pushVelocities.put(entity, pushVel);
        }
        pushVel.x += pushX;
        pushVel.y += pushY;
    }

    /**
     * 位移完成后清零推开速度分量：实体不再重叠的下一帧起不再有任何排斥作用
     */
    private void clearPushForces () {
        for (Map.Entry<Entity<?>, Vector2> entry : this.pushVelocities.entrySet()) {
            Entity<?> entity = entry.getKey();
            Vector2 pushVel = entry.getValue();
            entity.setVelX(entity.getVelX() - pushVel.x);
            entity.setVelY(entity.getVelY() - pushVel.y);
        }
        this.pushVelocities.clear();
    }

    /**
     * 推开分量被墙碰撞清零后标记为已消耗
     * <p>
     * 推开速度叠加进 velocity 后，若该轴撞墙被 {@code setVelX/setVelY(0)} 清零，
     * clearPushForces 再减去推开分量会产生反向速度（贴墙被推时反向滑）。
     * 撞墙时把该轴推开分量置 0，清除阶段便不再减它
     */
    private void markPushConsumed (Entity<?> entity, boolean xAxis) {
        Vector2 pushVel = this.pushVelocities.get(entity);
        if (pushVel == null) return;
        if (xAxis) pushVel.x = 0f;
        else pushVel.y = 0f;
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
            // X/Y 轴一起分步移动，再统一做最小侵入轴修正
            if (Math.abs(stepX) > EPS) {
                rect.x += stepX;
            }
            if (Math.abs(stepY) > EPS) {
                rect.y += stepY;
            }

            // 返回被分离的轴（位标记：1=X，2=Y）
            int separatedAxis = this.fixCollisions(rect);
            if ((separatedAxis & 1) != 0) {
                stepX = 0;
                entity.setVelX(0);
                //推开分量也被墙清零了，标记已消耗（否则 clearPushForces 会减出反向速度）
                this.markPushConsumed(entity, true);
            }
            if ((separatedAxis & 2) != 0) {
                stepY = 0;
                entity.setVelY(0);
                this.markPushConsumed(entity, false);
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
     * 修正碰撞并返回被分离的轴（位标记：1=X，2=Y，0=无）
     * <p>
     * 对每个重叠墙取最小侵入轴分离（分离方向由侵入来源决定，把碰撞箱推出墙）：
     * 贴墙（侧向轻微侵入）时沿侧向分离，沿墙方向的移动不受影响；
     * 之前按"移动轴"计算重叠量，会把贴墙时"相邻包含"误判为移动轴侵入，
     * 导致玩家贴墙沿墙移动时每帧被垂直拉走（"贴墙滑行"）
     */
    private int fixCollisions (Rectangle hitbox) {
        Array<Wall<?>> collidingWalls = this.getCollidingWalls(hitbox);
        if (collidingWalls.isEmpty()) {
            return 0;
        }

        int fixes = 0;
        int separatedAxis = 0;

        while (!collidingWalls.isEmpty() && fixes < MAX_FIXES) {
            for (Wall<?> wall : collidingWalls) {
                Rectangle wallBox = wall.getHitboxRectangle();
                if (!hitbox.overlaps(wallBox)) continue;

                //两轴侵入量（碰撞箱进入墙的量）
                float overlapX = Math.min(
                    hitbox.x + hitbox.width - wallBox.x,
                    wallBox.x + wallBox.width - hitbox.x
                );
                float overlapY = Math.min(
                    hitbox.y + hitbox.height - wallBox.y,
                    wallBox.y + wallBox.height - hitbox.y
                );
                if (overlapX <= EPS && overlapY <= EPS) continue;

                if (overlapX < overlapY) {
                    //沿 X 轴分离：侵入来自右边缘（rect 在墙左侧）则向左推，反之向右推
                    boolean enterFromLeft = (hitbox.x + hitbox.width - wallBox.x)
                        <= (wallBox.x + wallBox.width - hitbox.x);
                    hitbox.x += enterFromLeft ? -overlapX - EPS : overlapX + EPS;
                    separatedAxis |= 1;
                } else {
                    //沿 Y 轴分离：侵入来自顶边缘（rect 在墙下方）则向下推，反之向上推
                    boolean enterFromBottom = (hitbox.y + hitbox.height - wallBox.y)
                        <= (wallBox.y + wallBox.height - hitbox.y);
                    hitbox.y += enterFromBottom ? -overlapY - EPS : overlapY + EPS;
                    separatedAxis |= 2;
                }
            }

            collidingWalls = this.getCollidingWalls(hitbox);
            fixes++;
        }

        return separatedAxis;
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

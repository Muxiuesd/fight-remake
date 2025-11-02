package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterEntityHurt;
import ttk.muxiuesd.registry.DamageTypes;
import ttk.muxiuesd.registry.EntityTypes;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.chunk.Chunk;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.wall.Wall;

/**
 * 子弹碰撞系统
 * <p>
 * 子弹与墙体或者实体的碰撞
 * */
public class BulletCollisionSystem extends WorldSystem {
    public final String TAG = this.getClass().getName();

    private EntitySystem es;
    private ChunkSystem cs;

    public BulletCollisionSystem (World world) {
        super(world);
        this.es = world.getSystem(EntitySystem.class);
        this.cs = world.getSystem(ChunkSystem.class);
    }

    @Override
    public void update(float delta) {
        this.checkBulletCollision(delta);
    }

    private void checkBulletCollision(float delta) {
        Array<Bullet> allBullets = new Array<>();
        allBullets.addAll(es.getPlayerBulletEntity());
        allBullets.addAll(es.getEnemyBulletEntity());

        for (Bullet bullet : allBullets) {
            if (!bullet.isAlive()) continue;

            // 根据子弹类型获取相关碰撞目标（墙体+实体）
            Array<Wall> relevantWalls = getRelevantWalls(delta, bullet);
            Array<LivingEntity<?>> relevantEntities = getRelevantEntities(bullet);

            // 分步更新并检测碰撞（墙体+实体）
            updateBulletWithEntityCollision(bullet, delta, relevantWalls, relevantEntities);
        }
    }
    // 优化墙体检测范围（不变）
    private Array<Wall> getRelevantWalls(float delta, Bullet bullet) {
        Array<Wall> relevantWalls = new Array<>();
        Rectangle bulletHitbox = bullet.getHitbox();

        Vector2 velocity = bullet.getVelocity();
        Rectangle pathBounds = new Rectangle(
            Math.min(bullet.x, bullet.x + velocity.x * delta),
            Math.min(bullet.y, bullet.y + velocity.y * delta),
            Math.abs(velocity.x * delta) + bulletHitbox.width,
            Math.abs(velocity.y * delta) + bulletHitbox.height
        );

        // 收集路径范围内的墙体（和之前逻辑一致）
        ChunkPosition startPos = cs.getChunkPosition(pathBounds.x, pathBounds.y);
        ChunkPosition endPos = cs.getChunkPosition(
            pathBounds.x + pathBounds.width,
            pathBounds.y + pathBounds.height
        );

        for (int x = startPos.getX() - 1; x <= endPos.getX() + 1; x++) {
            for (int y = startPos.getY() - 1; y <= endPos.getY() + 1; y++) {
                Chunk chunk = cs.getChunk(x, y);
                if (chunk == null) continue;

                chunk.traversal((wx, wy) -> {
                    Wall wall = chunk.getWall(wx, wy);
                    if (wall != null && wall.getHitbox() != null &&
                        pathBounds.overlaps(wall.getHitbox())) {
                        relevantWalls.add(wall);
                    }
                });
            }
        }
        return relevantWalls;
    }

    // 根据子弹类型获取需要检测的实体（玩家子弹→敌人；敌人子弹→玩家）
    private Array<LivingEntity<?>> getRelevantEntities (Bullet bullet) {
        Array<LivingEntity<?>> targets = new Array<>();
        if (bullet.getOwner() == null) return targets;

        // 玩家的子弹→检测敌人
        if (bullet.getOwner().getType() == EntityTypes.PLAYER) {
            targets.addAll(es.getEnemyEntity());
        }
        // 敌人的子弹→检测玩家
        else if (bullet.getOwner().getType() == EntityTypes.ENEMY) {
            Player player = es.getPlayer();
            if (player != null && !player.isDeath()) {
                targets.add(player);
            }
        }

        // 过滤掉不在子弹路径范围内的实体（优化性能）
        return this.filterEntitiesByPath(bullet, targets);
    }

    // 过滤子弹路径范围外的实体（减少检测量）
    private Array<LivingEntity<?>> filterEntitiesByPath(Bullet bullet, Array<LivingEntity<?>> entities) {
        Array<LivingEntity<?>> filtered = new Array<>();
        Rectangle bulletHitbox = bullet.getHitbox();
        Vector2 velocity = bullet.getVelocity();
        float delta = 1f; // 预测1帧内的移动距离（可根据实际帧率调整）

        // 计算子弹移动路径的边界框
        Rectangle pathBounds = new Rectangle(
            Math.min(bullet.x, bullet.x + velocity.x * delta),
            Math.min(bullet.y, bullet.y + velocity.y * delta),
            Math.abs(velocity.x * delta) + bulletHitbox.width,
            Math.abs(velocity.y * delta) + bulletHitbox.height
        );

        // 只保留路径范围内的实体
        for (LivingEntity<?> entity : entities) {
            if (entity.getHitbox() != null && pathBounds.overlaps(entity.getHitbox())) {
                filtered.add(entity);
            }
        }
        return filtered;
    }

    // 扩展子弹更新逻辑，同时检测墙体和实体碰撞
    private void updateBulletWithEntityCollision(Bullet bullet, float deltaTime,
                                                 Array<Wall> walls, Array<LivingEntity<?>> targets) {
        Vector2 velocity = bullet.getVelocity();
        Rectangle bulletHitbox = bullet.getHitbox();

        // 1. 计算分步参数（防止隧穿）
        float totalDistance = velocity.len() * deltaTime;
        float maxStep = bulletHitbox.width * 0.5f; // 步长≤子弹一半大小，确保不穿透
        int steps = Math.max(1, (int) Math.ceil(totalDistance / maxStep));
        float stepDelta = deltaTime / steps;
        Vector2 step = new Vector2(
            velocity.x * stepDelta,
            velocity.y * stepDelta
        );

        // 2. 分步移动并检测碰撞
        for (int i = 0; i < steps; i++) {
            float prevX = bullet.x;
            float prevY = bullet.y;

            // 移动一步
            bullet.x += step.x;
            bullet.y += step.y;
            bulletHitbox.setPosition(bullet.x, bullet.y);

            // 3. 检测与墙体碰撞（复用之前的逻辑）
            if (checkWallCollision(bullet, walls, prevX, prevY)) {
                destroyBullet(bullet);
                return;
            }

            // 4. 检测与目标实体碰撞（新增逻辑）
            if (checkEntityCollision(bullet, targets, prevX, prevY)) {
                destroyBullet(bullet);
                return;
            }
        }
    }

    // 检测与墙体的碰撞
    private boolean checkWallCollision(Bullet bullet, Array<Wall> walls, float prevX, float prevY) {
        Rectangle bulletHitbox = bullet.getHitbox();
        for (Wall wall : walls) {
            Rectangle wallHitbox = wall.getHitbox();
            if (bulletHitbox.overlaps(wallHitbox)) {
                // 回退到碰撞前位置
                bullet.x = prevX;
                bullet.y = prevY;
                bulletHitbox.setPosition(prevX, prevY);
                return true; // 碰撞发生
            }
        }
        return false;
    }

    // 检测与目标实体的碰撞（并处理伤害）
    private boolean checkEntityCollision(Bullet bullet, Array<LivingEntity<?>> targets, float prevX, float prevY) {
        Rectangle bulletHitbox = bullet.getHitbox();
        for (LivingEntity<?> target : targets) {
            if (target.isDeath()) continue;

            Rectangle targetHitbox = target.getHitbox();
            if (bulletHitbox.overlaps(targetHitbox)) {
                // 回退子弹位置（视觉上更自然）
                bullet.x = prevX;
                bullet.y = prevY;
                bulletHitbox.setPosition(prevX, prevY);

                // 处理伤害（根据实体类型调用不同逻辑）
                target.applyDamage(DamageTypes.BULLET, bullet);
                return true; // 碰撞发生
            }
        }
        return false;
    }

    // 销毁子弹逻辑（不变）
    private void destroyBullet(Bullet bullet) {
        es.remove(bullet);
        //Log.print(TAG(), "子弹在：" + bullet.x + ", " + bullet.y + " 碰撞");
    }

    /**
     * 调用实体受攻击的事件
     * */
    private void callEntityAttackedEvent (Entity<?> attackedObject, Entity<?> victim) {
        EventBus.post(EventTypes.ENTITY_HURT, new EventPosterEntityHurt(getWorld(), attackedObject, victim));
    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        EntitySystem es = getWorld().getSystem(EntitySystem.class);
        Array<Bullet> playerBulletEntity = es.getPlayerBulletEntity();
        Array<Bullet> enemyBulletEntity = es.getEnemyBulletEntity();
        for (Bullet bullet : playerBulletEntity) {
            Rectangle hurtbox = bullet.hitbox;
            batch.rect(hurtbox.getX(), hurtbox.getY(), hurtbox.getWidth(), hurtbox.getHeight());
        }
        for (Bullet bullet : enemyBulletEntity) {
            Rectangle hurtbox = bullet.hitbox;
            batch.rect(hurtbox.getX(), hurtbox.getY(), hurtbox.getWidth(), hurtbox.getHeight());
        }
    }
}

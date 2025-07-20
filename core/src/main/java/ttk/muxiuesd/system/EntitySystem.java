package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterEntityDeath;
import ttk.muxiuesd.interfaces.render.IWorldGroundEntityRender;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.EntityTypes;
import ttk.muxiuesd.registry.RenderLayers;
import ttk.muxiuesd.render.RenderLayer;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemPickUpState;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 实体的管理系统，负责实体的储存以及更新，但不负责渲染
 * */
public class EntitySystem extends WorldSystem implements IWorldGroundEntityRender {
    public final String TAG = EntitySystem.class.getName();

    private boolean renderHitbox = false;

    private final Array<Entity<?>> _delayAdd = new Array<>();
    private final Array<Entity<?>> _delayRemove = new Array<>();

    private final Array<Entity<?>> entities = new Array<>();   //所有实体
    private final Array<Entity<?>> updatableEntity = new Array<>();

    //实体管理组map，每一种注册过的实体类型都有一个管理组，key为实体类型，value为该实体类型的持有实体管理数组
    private final ConcurrentHashMap<EntityType<?>, Array<? extends Entity<?>>> entityTypes = new ConcurrentHashMap<>();

    //可渲染的实体组map，key为渲染层级，value为该层级下所有要渲染的实体
    private final ConcurrentHashMap<RenderLayer, Array<Entity<?>>> renderableEntities = new ConcurrentHashMap<>();

    public EntitySystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        //将注册的实体类型的管理组加进来
        for (EntityType<?> entityType : Registries.ENTITY_TYPE.getMap().values()) {
            this.entityTypes.put(entityType, entityType.createEntityArray());
        }

        PlayerSystem ps = (PlayerSystem) getManager().getSystem("PlayerSystem");
        Player player = ps.getPlayer();
        player.setEntitySystem(this);
        this.add(player);

        this.renderableEntities.put(RenderLayers.ENTITY_UNDERGROUND, new Array<>());
        this.renderableEntities.put(RenderLayers.ENTITY_GROUND, new Array<>());

        Log.print(TAG, "EntitySystem初始化完成！");
    }

    /**
     * 添加实体
     * */
    public <T extends Entity> void add (T entity) {
        //防止重复添加
        if (this._delayAdd.contains(entity, true) || this.entities.contains(entity, true)) return;

        this._delayAdd.add(entity);
    }

    /**
     * 移除实体
     * */
    public <T extends Entity> void remove (T entity) {
        if (this.entities.contains(entity, true)) {
            this._delayRemove.add(entity);
        }
    }

    /**
     * 延迟添加实体防止并发修改异常
     * @param entity 实体
     */
    private <T extends Entity> void _add (T entity) {
        Array<T> entityArray = (Array<T>) this.getEntityArray(entity.getType());
        if (! entityArray.contains(entity, true)) {
            //避免重复添加
            entityArray.add(entity);
        }
        this.entities.add(entity);
        this.updatableEntity.add(entity);
        //把实体添加进相应的渲染层级
        if (this.renderableEntities.containsKey(entity.getRenderLayer()))
            this.renderableEntities.get(entity.getRenderLayer()).add(entity);
        //防止没有指定实体系统
        entity.setEntitySystem(this);
        entity.initialize();
    }

    /**
     * 延迟移除实体防止并发修改异常
     * @param entity 实体
     */
    private <T extends Entity> void _remove (T entity) {
        Array<T> entityArray = (Array<T>) this.getEntityArray(entity.getType());
        if (entityArray.contains(entity, true)) {
            //避免重复移除
            entityArray.removeValue(entity, true);
        }
        this.entities.removeValue(entity, true);
        this.updatableEntity.removeValue(entity, true);
        //把实体移除出渲染层级
        this.renderableEntities.get(entity.getRenderLayer()).removeValue(entity, true);
    }

    @Override
    public void update(float delta) {
        if (KeyBindings.HitboxDisplay.wasJustPressed()) this.renderHitbox = !this.renderHitbox;

        if (!_delayRemove.isEmpty()) {
            for (Entity entity : this._delayRemove) {
                _remove(entity);
            }
            _delayRemove.clear();
        }
        if (!_delayAdd.isEmpty()) {
            for (Entity entity : this._delayAdd) {
                _add(entity);
            }
            _delayAdd.clear();
        }

        for (Entity entity : this.updatableEntity) {
            //先把所有实体更新一次
            if (!(entity instanceof ItemEntity)) {
                //对于物品实体进行当前速度更新
                this.calculateEntityCurSpeed(entity, (ChunkSystem) getManager().getSystem("ChunkSystem"), delta);
            }
            entity.update(delta);
            //细化实体更新
            //对于活物实体
            if (entity instanceof LivingEntity livingEntity) {
                this.updateLivingEntity(livingEntity, delta);
            }
            //对于物品实体
            else if (entity instanceof ItemEntity itemEntity) {
                this.updateItemEntity(itemEntity, delta);
            }
        }
    }

    private void updateLivingEntity(LivingEntity livingEntity, float delta) {
        //移除死亡的实体,玩家死亡移除不在这个逻辑里
        if (livingEntity.isDeath()) {
            EventBus.post(EventTypes.ENTITY_DEATH, new EventPosterEntityDeath(getWorld(), livingEntity));
            this.remove(livingEntity);
        }
    }

    private void updateItemEntity (ItemEntity itemEntity, float delta) {
        //移除超过存活时间的物品实体
        if (itemEntity.getLivingTime() > Fight.MAX_ITEM_ENTITY_LIVING_TIME) {
            this.remove(itemEntity);
            //跳过这个物品实体的 其他操作
            return;
        }
        Player player = this.getPlayer();
        //需要被丢弃物品实体存在时间超过三秒，防止一丢弃就被自动捡回来
        if (itemEntity.getLivingTime() > Fight.ITEM_ENTITY_PICKUP_SPAN) {
            //当物品实体与玩家的碰撞箱相碰就是捡起
            if (itemEntity.hitbox.overlaps(player.hitbox)) {
                ItemStack itemStack = itemEntity.getItemStack();
                ItemPickUpState state = player.pickUpItem(itemStack);
                if (state == ItemPickUpState.WHOLE) {
                    this.remove(itemEntity);
                    AudioPlayer.getInstance().playSound(Fight.getId("pop"));
                    //整个捡起来就没必要执行下面的代码了
                    return;
                }else if (state == ItemPickUpState.PARTIAL) {
                    //部分捡起时刷新存在时间
                    itemEntity.setLivingTime(0);
                }
                //捡起失败则什么也没发生
                itemEntity.setVelocity(0, 0);
            }

            float distance = Util.getDistance(itemEntity, player);
            if (distance <= Fight.PICKUP_RANGE && !player.getBackpack().isFull(itemEntity.getItemStack())) {
                //在捡起范围内，并且对于这个物品来说背包还没满，让物品实体朝向玩家运动
                Direction direction = new Direction(itemEntity.getCenter(), player.getCenter());
                itemEntity.setVelocity(direction);
                itemEntity.setSpeed(7.7f);
            }
        }

        this.calculateItemEntityCurSpeed(itemEntity, (ChunkSystem) getManager().getSystem("ChunkSystem"), delta);
    }

    /**
     * 对实体进行当前速度计算
     * */
    private void calculateEntityCurSpeed (Entity entity, ChunkSystem cs, float delta) {
        //对于速度为0的实体不进行速度更新
        if (entity.getSpeed() <= 0) return;

        //计算脚下方块摩擦对速度的影响
        Vector2 center = entity.getCenter();
        Block block = cs.getBlock(center.x, center.y);
        if (block == null) return;
        float friction = block.getProperty().getFriction();
        float curSpeed = entity.getSpeed() * friction;
        //速度过小直接为0
        if (curSpeed < 0.0000001) {
            entity.setSpeed(0);
            entity.setCurSpeed(0);
            return;
        }
        entity.setCurSpeed(curSpeed);
    }

    /**
     * 对物品实体进行当前速度计算
     * */
    private void calculateItemEntityCurSpeed (ItemEntity entity, ChunkSystem cs, float delta) {
        //对于速度为0的实体不进行速度更新
        if (entity.getSpeed() <= 0) return;

        float curSpeed = entity.getSpeed();
        //实体在地面上
        if (entity.isOnGround()) {
            //计算脚下方块摩擦对速度的影响
            Vector2 center = entity.getCenter();
            Block block = cs.getBlock(center.x, center.y);
            if (block == null) return;
            curSpeed *= block.getProperty().getFriction();
        }
        //速度过小直接为0
        if (curSpeed < 0.0000001) {
            entity.setSpeed(0);
            entity.setCurSpeed(0);
            return;
        }
        entity.setCurSpeed(curSpeed);
        //entity.setSpeed(entity.getSpeed() - curSpeed * delta * 0.8f);
        entity.setSpeed((float) (entity.getSpeed() * Math.pow(0.98, delta * 60)));
    }

    @Override
    public void render (Batch batch, ShapeRenderer shapeRenderer) {
        this.renderShape(shapeRenderer);
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        if (this.renderHitbox) {
            for (Entity entity : this.entities) {
                Rectangle hitbox = entity.getHitbox();
                batch.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
                Vector2 entityCenter = entity.getCenter();
                if (entity instanceof LivingEntity livingEntity) {
                    batch.line(entityCenter, new Vector2(entityCenter).add(livingEntity.getDirection().scl(1)));
                }else {
                    batch.line(entityCenter, new Vector2(entityCenter).add(entity.getVelocity().scl(1)));
                }
            }
        }
    }

    @Override
    public void dispose() {
        for (Entity<?> entity : this.getEntities()) {
            entity.dispose();
        }
    }

    public Player getPlayer() {
        PlayerSystem ps = (PlayerSystem) getManager().getSystem("PlayerSystem");
        return ps.getPlayer();
    }

    /**
     * 获取所有的实体
     * */
    public Array<Entity<?>> getEntities () {
        return this.entities;
    }

    /**
     * 获取实体类型相应的管理组
     * */
    public <T extends Entity<?>> Array<T> getEntityArray (EntityType<T> type) {
        return (Array<T>) this.entityTypes.get(type);
    }

    public Array<Enemy<?>> getEnemyEntity () {
        return this.getEntityArray(EntityTypes.ENEMY);
    }

    public Array<Bullet> getPlayerBulletEntity () {
        return this.getEntityArray(EntityTypes.PLAYER_BULLET);
    }

    public Array<Bullet> getEnemyBulletEntity () {
        return this.getEntityArray(EntityTypes.ENEMY_BULLET);
    }

    public ConcurrentHashMap<RenderLayer, Array<Entity<?>>> getRenderableEntities () {
        return this.renderableEntities;
    }

    @Override
    public int getRenderPriority () {
        return 10000;
    }
}

package ttk.muxiuesd.system;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterBulletShoot;
import ttk.muxiuesd.event.poster.EventPosterEntityDeath;
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
import ttk.muxiuesd.world.entity.Group;
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
public class EntitySystem extends WorldSystem/* implements IWorldGroundEntityRender */{
    public final String TAG = EntitySystem.class.getName();

    private final Array<Entity> _delayAdd = new Array<>();
    private final Array<Entity> _delayRemove = new Array<>();

    private final Array<Entity> entities = new Array<>();   //所有实体
    private final Array<Entity> updatableEntity = new Array<>();

    //实体管理组map，每一种注册过的实体类型都有一个管理组，key为实体类型，value为该实体类型的持有数组
    private final ConcurrentHashMap<EntityType<?>, Array<?>> entityTypes = new ConcurrentHashMap<>();

    //可渲染的实体组map，key为渲染层级，value为该层级下所有要渲染的实体
    private final ConcurrentHashMap<RenderLayer, Array<Entity>> renderableEntities = new ConcurrentHashMap<>();

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
        this.add(EntityTypes.PLAYER, player);

        this.renderableEntities.put(RenderLayers.ENTITY_UNDERGROUND, new Array<>());
        this.renderableEntities.put(RenderLayers.ENTITY_GROUND, new Array<>());

        Log.print(TAG, "EntitySystem初始化完成！");
    }

    /**
     * 将实体加进对应的管理组，因为一种实体可以隶属于不同的管理组进而有不同的行为
     * */
    public <T extends Entity> void add (EntityType<T> type, T entity) {
        this.add(entity);
        //避免重复添加
        if (!this.getEntityArray(type).contains(entity, true)) this.getEntityArray(type).add(entity);
    }

    public <T extends Entity> void remove (EntityType<T> type, T entity) {
        this.remove(entity);
        //避免重复移除
        if (this.getEntityArray(type).contains(entity, true)) this.getEntityArray(type).removeValue(entity, true);
    }

    /**
     * 简单的添加实体
     * */
    public void add (Entity entity) {
        //防止重复添加
        if (this._delayAdd.contains(entity, true) || this.entities.contains(entity, true)) return;

        this._delayAdd.add(entity);
    }

    /**
     * 简单的移除实体
     * */
    public void remove (Entity entity) {
        this._delayRemove.add(entity);
    }

    /**
     * 延迟添加实体防止并发修改异常
     *
     * @param entity 实体
     */
    private void _add (Entity entity) {
        //优先进行实体组类型判断
        if (entity.group == Group.player) {
            //玩家组
            if (entity instanceof Bullet bullet) {
                //玩家的子弹
                //this.playerBulletEntity.add(bullet);
                this.addEntity(EntityTypes.PLAYER_BULLET, bullet);
                this.callBulletShootEvent(bullet.owner, bullet);
            } else if (entity instanceof Player player) {
                //TODO
            }
        } else if (entity.group == Group.enemy) {
            //敌人组
            if (entity instanceof Enemy enemy) {
                //this.enemyEntity.add(enemy);
                this.addEntity(EntityTypes.ENEMY, enemy);
            } else if (entity instanceof Bullet bullet) {
                //this.enemyBulletEntity.add(bullet);
                this.addEntity(EntityTypes.ENEMY_BULLET, bullet);
                this.callBulletShootEvent(bullet.owner, bullet);
            }
        } else if (entity instanceof ItemEntity itemEntity) {
            this.addEntity(EntityTypes.ITEM_ENTITY, itemEntity);
            //this.itemEntity.add(itemEntity);
        }

        this.entities.add(entity);
        this.updatableEntity.add(entity);
        //把实体添加进相应的渲染层级
        if (this.renderableEntities.containsKey(entity.getRenderLayer()))
            this.renderableEntities.get(entity.getRenderLayer()).add(entity);
    }

    /**
     * 延迟移除实体防止并发修改异常
     *
     * @param entity 实体
     */
    private void _remove (Entity entity) {
        //优先进行实体组类型判断
        if (entity.group == Group.enemy) {
            //绝大部分移除调用都是敌人相关的子弹实体
            if (entity instanceof Bullet bullet) {
                //大部分是子弹
                this.removeEntity(EntityTypes.ENEMY_BULLET, bullet);
                //this.enemyBulletEntity.removeValue(bullet, true);
            }else if (entity instanceof Enemy enemy) {
                this.removeEntity(EntityTypes.ENEMY, enemy);
                //this.enemyEntity.removeValue(enemy, true);
            }
        } else if (entity.group == Group.player) {
            //其次是玩家相关的实体
            if (entity instanceof Bullet bullet) {
                //大部分是子弹
                this.removeEntity(EntityTypes.PLAYER_BULLET, bullet);
                //this.playerBulletEntity.removeValue(bullet, true);
            }else {
                //TODO 暂时不能移除玩家
            }
        } else if (entity instanceof ItemEntity itemEntity) {
            //剩下就是物品实体
            this.removeEntity(EntityTypes.ITEM_ENTITY, itemEntity);
            //this.itemEntity.removeValue(itemEntity, true);
        }

        this.entities.removeValue(entity, true);
        this.updatableEntity.removeValue(entity, true);
        //把实体移除出渲染层级
        this.renderableEntities.get(entity.getRenderLayer()).removeValue(entity, true);
    }

    @Override
    public void update(float delta) {
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
            this.callEntityDeadEvent(livingEntity);
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
            }

            float distance = Util.getDistance(itemEntity, player);
            if (distance <= Fight.PICKUP_RANGE) {
                //在捡起范围内，让物品实体朝向玩家运动
                Direction direction = new Direction(itemEntity.getCenter(), player.getCenter());
                itemEntity.setVelocity(direction);
                itemEntity.setSpeed(16f);
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

    /*@Override
    public void draw(Batch batch) {
        for (Entity entity : this.drawableEntity) {
            entity.draw(batch);
        }
    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        for (Entity entity : this.getEntities()) {
            entity.renderShape(batch);
        }
    }

    @Override
    public void render (Batch batch, ShapeRenderer shapeRenderer) {
        this.draw(batch);
        this.renderShape(shapeRenderer);
    }

    @Override
    public int getRenderPriority () {
        return 100;
    }*/

    @Override
    public void dispose() {
        for (Entity entity : this.getEntities()) {
            entity.dispose();
        }
    }

    /**
     * 调用事件
     * */
    public void callBulletShootEvent (Entity shooter, Bullet bullet) {
        //EventBus.getInstance().callEvent(EventBus.EventType.BulletShoot, shooter, bullet);
        ttk.muxiuesd.event.EventBus.post("BulletShoot", new EventPosterBulletShoot(getWorld(), shooter, bullet));
    }

    public void callEntityDeadEvent (LivingEntity deadEntity) {
        //EventBus.getInstance().callEvent(EventBus.EventType.EntityDeath, deadEntity);
        EventBus.post(EventTypes.ENTITY_DEATH, new EventPosterEntityDeath(getWorld(), deadEntity));
    }

    public Player getPlayer() {
        PlayerSystem ps = (PlayerSystem) getManager().getSystem("PlayerSystem");
        return ps.getPlayer();
    }

    public Array<Entity> getEntities () {
        return this.entities;
    }

    /**
     * 把实体加进对应的实体类型管理组里面
     * */
    private <T extends Entity> void addEntity (EntityType<T> type, T entity) {
        this.getEntityArray(type).add(entity);
    }

    /**
     * 把实体从对应的实体类型管理组里面移除
     * */
    private <T extends Entity> void removeEntity (EntityType<T> type, T entity) {
        this.getEntityArray(type).removeValue(entity, true);
    }

    /**
     * 获取实体类型相应的管理组
     * */
    public <T extends Entity> Array<T> getEntityArray (EntityType<T> type) {
        return (Array<T>) this.entityTypes.get(type);
    }

    public Array<Enemy> getEnemyEntity () {
        return this.getEntityArray(EntityTypes.ENEMY);
    }

    public Array<Bullet> getPlayerBulletEntity () {
        return this.getEntityArray(EntityTypes.PLAYER_BULLET);
    }

    public Array<Bullet> getEnemyBulletEntity () {
        return this.getEntityArray(EntityTypes.ENEMY_BULLET);
    }

    public ConcurrentHashMap<RenderLayer, Array<Entity>> getRenderableEntities () {
        return this.renderableEntities;
    }
}

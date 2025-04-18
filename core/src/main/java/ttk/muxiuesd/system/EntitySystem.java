package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.bullet.Bullet;
import ttk.muxiuesd.world.entity.enemy.Slime;
import ttk.muxiuesd.world.event.EventBus;
import ttk.muxiuesd.world.item.ItemPickUpState;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 实体系统
 * */
public class EntitySystem extends WorldSystem {
    public final String TAG = EntitySystem.class.getName();

    private final Array<Entity> _delayAdd = new Array<>();
    private final Array<Entity> _delayRemove = new Array<>();

    private final Array<Entity> entities = new Array<>();

    public Array<LivingEntity> enemyEntity = new Array<>();
    public Array<Bullet> playerBulletEntity = new Array<>();
    public Array<Bullet> enemyBulletEntity = new Array<>();
    public Array<ItemEntity> itemEntity = new Array<>();

    private final Array<Entity> updatableEntity = new Array<>();
    private final Array<Entity> drawableEntity = new Array<>();

    public EntitySystem(World world) {
        super(world);
    }

    @Override
    public void initialize () {
        PlayerSystem ps = (PlayerSystem) getManager().getSystem("PlayerSystem");
        Player player = ps.getPlayer();
        player.setEntitySystem(this);
        this.add(player);

        Slime slime = new Slime();
        slime.setEntitySystem(this);
        slime.setBounds((float) (player.x + 5 * Math.cos(Util.randomRadian())),
            (float) (player.y + 5 * Math.sin(Util.randomRadian())),
            1, 1);
        this.add(slime);

        Log.print(TAG, "EntitySystem初始化完成！");
    }


    public void add(Entity entity) {
        _delayAdd.add(entity);
    }

    public void remove(Entity entity) {
        _delayRemove.add(entity);
    }

    /**
     * 延迟添加实体防止并发修改异常
     *
     * @param entity 实体
     */
    private void _add(Entity entity) {
        if (entity instanceof Bullet) {
            Bullet bullet = (Bullet) entity;
            if (bullet.group == Group.player) {
                this.playerBulletEntity.add(bullet);
                this.callBulletShootEvent(this.getPlayer(), bullet);
            }
            if (bullet.group == Group.enemy) {
                this.enemyBulletEntity.add(bullet);
                this.callBulletShootEvent(bullet.owner, bullet);
            }
        }else if (entity.group == Group.enemy) {
            this.enemyEntity.add((LivingEntity) entity);
        }
        if (entity instanceof ItemEntity) {
            this.itemEntity.add((ItemEntity) entity);
        }

        this.updatableEntity.add(entity);
        this.drawableEntity.add(entity);
        this.entities.add(entity);
    }

    /**
     * 延迟移除实体防止并发修改异常
     *
     * @param entity 实体
     */
    private void _remove(Entity entity) {
        //优先进行实体组类型判断
        if (entity.group == Group.enemy) {
            //绝大部分移除调用都是敌人相关的实体
            if (entity instanceof Bullet) {
                //大部分是子弹
                Bullet bullet = (Bullet) entity;
                this.enemyBulletEntity.removeValue(bullet, true);
            }else {
                this.enemyEntity.removeValue((LivingEntity) entity, true);
            }
        }
        if (entity.group == Group.player) {
            //其次是玩家相关的实体
            if (entity instanceof Bullet) {
                //大部分是子弹
                Bullet bullet = (Bullet) entity;
                this.playerBulletEntity.removeValue(bullet, true);
            }else {
                //TODO 暂时不能移除玩家
            }
        }
        if (entity instanceof ItemEntity) {
            this.itemEntity.removeValue((ItemEntity) entity, true);
        }

        this.updatableEntity.removeValue(entity, true);
        this.drawableEntity.removeValue(entity, true);
        this.entities.removeValue(entity, true);
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
            entity.update(delta);
            //细化实体更新
            //对于活物实体
            if (entity instanceof LivingEntity) {
                this.updateLivingEntity((LivingEntity) entity, delta);
            }
            //对于物品实体
            else if (entity instanceof ItemEntity) {
                this.updateItemEntity((ItemEntity) entity, delta);
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
        //需要被丢弃物品实体存在时间超过三秒，防止一丢弃就被自动捡回来
        if (itemEntity.getLivingTime() > Fight.ITEM_ENTITY_PICKUP_SPAN) {
            float distance = Util.getDistance(itemEntity, this.getPlayer());
            if (distance <= Fight.PICKUP_RANGE) {
                ItemStack itemStack = itemEntity.getItemStack();
                ItemPickUpState state = this.getPlayer().pickUpItem(itemStack);
                if (state == ItemPickUpState.WHOLE) {
                    this.remove(itemEntity);
                }else if (state == ItemPickUpState.PARTIAL) {
                    //部分捡起时刷新存在时间
                    itemEntity.setLivingTime(0);
                }
                //捡起失败则什么也没发生
            }
        }
    }

    @Override
    public void draw(Batch batch) {
        for (Entity entity : this.drawableEntity) {
            entity.draw(batch);
        }
    }

    @Override
    public void renderShape(ShapeRenderer batch) {

    }

    @Override
    public void dispose() {
        for (Entity entity : this.entities) {
            entity.dispose();
        }
    }

    /**
     * 调用事件
     * */
    public void callBulletShootEvent (Entity shooter, Bullet bullet) {
        EventBus.getInstance().callEvent(EventBus.EventType.BulletShoot, shooter, bullet);
    }

    public void callEntityDeadEvent (LivingEntity deadEntity) {
        EventBus.getInstance().callEvent(EventBus.EventType.EntityDeath, deadEntity);
    }

    public Player getPlayer() {
        PlayerSystem ps = (PlayerSystem) getManager().getSystem("PlayerSystem");
        return ps.getPlayer();
    }

    public Array<Entity> getEntities () {
        return this.entities;
    }
}

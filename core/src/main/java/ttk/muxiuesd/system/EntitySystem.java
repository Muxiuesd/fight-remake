package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.bullet.Bullet;
import ttk.muxiuesd.world.entity.enemy.Slime;
import ttk.muxiuesd.world.event.EventBus;
import ttk.muxiuesd.world.event.EventGroup;
import ttk.muxiuesd.world.event.abs.BulletShootEvent;
import ttk.muxiuesd.world.event.abs.EntityDeathEvent;

import java.util.HashSet;

/**
 * 实体系统
 * */
public class EntitySystem extends WorldSystem {
    public final String TAG = EntitySystem.class.getName();

    private final Array<Entity> _delayAdd = new Array<>();
    private final Array<Entity> _delayRemove = new Array<>();

    private final Array<Entity> entities = new Array<>();

    public Array<Entity> enemyEntity = new Array<>();
    public Array<Bullet> playerBulletEntity = new Array<>();
    public Array<Bullet> enemyBulletEntity = new Array<>();

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
        /*if (entity instanceof Player) {
            this.player = (Player) entity;
        }*/
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
            this.enemyEntity.add(entity);
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
                this.enemyEntity.removeValue(entity, true);
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
            entity.update(delta);

            //移除死亡的实体,玩家死亡移除不在这个逻辑里
            if (!(entity instanceof Player) && entity.isDeath()) {
                this.callEntityDeadEvent(entity);
                this.remove(entity);
            }
        }
    }

    @Override
    public void draw(Batch batch) {
        for (Entity entity : drawableEntity) {
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
        EventGroup<BulletShootEvent> eventGroup = EventBus.getInstance().getEventGroup(EventBus.EventType.BulletShoot);
        HashSet<BulletShootEvent> events = eventGroup.getEvents();
        for (BulletShootEvent event :events) {
            event.call(shooter, bullet);
        }
    }

    public void callEntityDeadEvent (Entity deadEntity) {
        EventGroup<EntityDeathEvent> eventGroup = EventBus.getInstance().getEventGroup(EventBus.EventType.EntityDeath);
        HashSet<EntityDeathEvent> events = eventGroup.getEvents();
        for (EntityDeathEvent event :events) {
            event.call(deadEntity);
        }
    }

    public Player getPlayer() {
        PlayerSystem ps = (PlayerSystem) getManager().getSystem("PlayerSystem");
        return ps.getPlayer();
    }

    public Array<Entity> getEntities () {
        return this.entities;
    }
}

package ttk.muxiuesd.system;

import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.event.*;

/**
 * 事件系统，隶属于world
 * */
public class EventSystem extends WorldSystem {
    public EventSystem(World world) {
        super(world);

    }

    @Override
    public void initialize () {
        this.initAllEvents();
    }

    /**
     * 初始化所有游戏内部的事件
     * <p>
     * 如：世界事件
     * */
    private void initAllEvents () {
        //EventBus bus = EventBus.getInstance();
        //添加游戏内事件
        //bus.addEvent(EventBus.EventType.TickUpdate, new EventWorldWorldTick());
        EventBus.subscribe(EventTypes.WORLD_TICK, new EventWorldWorldTick());

        //bus.addEvent(EventBus.EventType.EntityAttacked, new EventPlayerAttacked());
        EventBus.subscribe(EventTypes.ENTITY_HURT, new EventPlayerAttacked());
        //bus.addEvent(EventBus.EventType.EntityAttacked, new EventSlimeAttacked(getWorld()));
        EventBus.subscribe(EventTypes.ENTITY_HURT, new EventSlimeAttacked());

        //bus.addEvent(EventBus.EventType.PlayerDeath, new EventPlayerDead());
        EventBus.subscribe(EventTypes.PLAYER_DEATH, new EventPlayerDead());
        //bus.addEvent(EventBus.EventType.EntityDeath, new EventSlimeDead(getWorld()));
        EventBus.subscribe(EventTypes.ENTITY_DEATH, new EventSlimeDead());
        //bus.addEvent(EventBus.EventType.BulletShoot, new EventPlayerShootBullet(getWorld()));
        EventBus.subscribe(EventTypes.BULLET_SHOOT, new EventPlayerShootBullet());
        //bus.addEvent(EventBus.EventType.BulletShoot, new EventEnemyShootBullet(getWorld()));
        EventBus.subscribe(EventTypes.BULLET_SHOOT, new EventEnemyShootBullet());

        //bus.addEvent(EventBus.EventType.BlockReplaceEvent, new EventBlockReplace());
        EventBus.subscribe(EventTypes.BLOCK_REPLACE, new EventBlockReplace());
    }
}

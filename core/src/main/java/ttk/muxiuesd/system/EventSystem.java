package ttk.muxiuesd.system;

import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.event.EventBus;
import ttk.muxiuesd.world.event.instance.EventEnemyShootBullet;
import ttk.muxiuesd.world.event.instance.EventPlayerShootBullet;
import ttk.muxiuesd.world.event.instance.EventSlimeDead;

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
        EventBus bus = EventBus.getInstance();
        //添加游戏内事件
        bus.addEvent(EventBus.EventType.EntityDeath, new EventSlimeDead(getWorld()));
        bus.addEvent(EventBus.EventType.BulletShoot, new EventPlayerShootBullet(getWorld()));
        bus.addEvent(EventBus.EventType.BulletShoot, new EventEnemyShootBullet(getWorld()));
    }
}

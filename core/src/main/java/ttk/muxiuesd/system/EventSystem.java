package ttk.muxiuesd.system;

import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;
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
        Log.print(TAG(), "世界内事件注册完成");
    }

    /**
     * 初始化所有游戏内部的事件
     * <p>
     * 如：世界事件
     * */
    private void initAllEvents () {
        //添加游戏内事件
        EventBus.subscribe(EventTypes.WORLD_TICK, new EventWorldWorldTick());
        EventBus.subscribe(EventTypes.ENTITY_HURT, new EventPlayerAttacked());
        EventBus.subscribe(EventTypes.ENTITY_HURT, new EventEnemyAttacked());
        EventBus.subscribe(EventTypes.PLAYER_DEATH, new EventPlayerDead());
        EventBus.subscribe(EventTypes.ENTITY_DEATH, new EventSlimeDead());
        EventBus.subscribe(EventTypes.ENTITY_DEATH, new EventLivingEntityDeath());
        EventBus.subscribe(EventTypes.BULLET_SHOOT, new EventPlayerShootBullet());
        EventBus.subscribe(EventTypes.BULLET_SHOOT, new EventEnemyShootBullet());
        EventBus.subscribe(EventTypes.BLOCK_REPLACE, new EventBlockReplace());
    }
}

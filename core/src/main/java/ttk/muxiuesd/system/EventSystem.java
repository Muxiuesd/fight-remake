package ttk.muxiuesd.system;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.event.Event;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.event.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 事件系统，隶属于world
 * */
public class EventSystem extends WorldSystem {
    /// 本世界已订阅的事件：事件类型 -> 该类型的订阅实例集合
    private final Map<String, Set<Event>> eventsByType = new HashMap<>();

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
        this.subscribe(EventTypes.WORLD_TICK, new EventWorldWorldTick())
            .subscribe(EventTypes.ENTITY_HURT, new EventPlayerAttacked())
            .subscribe(EventTypes.ENTITY_HURT, new EventEnemyAttacked())
            .subscribe(EventTypes.PLAYER_DEATH, new EventPlayerDead())
            .subscribe(EventTypes.ENTITY_DEATH, new EventSlimeDead())
            .subscribe(EventTypes.ENTITY_DEATH, new EventLivingEntityDeath())
            .subscribe(EventTypes.BULLET_SHOOT, new EventPlayerShootBullet())
            .subscribe(EventTypes.BULLET_SHOOT, new EventEnemyShootBullet())
            .subscribe(EventTypes.BLOCK_REPLACE, new EventBlockReplace());
    }

    /**
     * 向当前的世界系统中订阅事件
     * <p>
     * 记录本世界订阅的事件实例与其事件类型，供 {@link #clearAllEvents()} 在销毁时统一取消
     * */
    public <T extends Event> EventSystem subscribe (String eventType, T event) {
        Set<Event> typeEvents = this.eventsByType.computeIfAbsent(eventType, k -> new HashSet<>());
        //本世界未订阅过该实例才真正订阅到全局事件总线
        if (typeEvents.add(event)) {
            EventBus.subscribe(eventType, event);
        }
        return this;
    }


    /**
     * 清理当前世界所有的事件
     * <p>
     * 取消本世界已订阅的全部事件实例（从全局事件总线移除），
     * 防止重进世界时全局总线累积重复订阅导致事件多次触发
     * */
    private void clearAllEvents () {
        this.eventsByType.forEach((eventType, typeEvents) ->
            typeEvents.forEach(event -> EventBus.unsubscribe(eventType, event)));
        this.eventsByType.clear();
    }

    /**
     * 世界销毁时清理本世界订阅的全部事件
     */
    @Override
    public void dispose () {
        this.clearAllEvents();
    }
}

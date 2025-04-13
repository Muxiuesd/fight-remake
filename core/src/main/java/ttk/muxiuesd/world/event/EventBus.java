package ttk.muxiuesd.world.event;

import ttk.muxiuesd.interfaces.Event;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.event.abs.*;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 事件总线
 * */
public class EventBus {
    public final String TAG = this.getClass().getName();

    public enum EventType {
        RegistryBlock, RegistryWall,RegistryEntity,
        BulletShoot, EntityAttacked, EntityDeath, PlayerDeath,
        BlockReplaceEvent,
        TickUpdate,
        KeyInput, ButtonInput
    }

    //单例模式，游戏里的唯一事件总线
    private static EventBus instance;
    private final HashMap<EventType, EventGroup> eventGroups;

    private EventBus() {
        eventGroups = new HashMap<>();
        eventGroups.put(EventType.BulletShoot, new EventGroup<BulletShootEvent>());
        eventGroups.put(EventType.EntityAttacked, new EventGroup<EntityAttackedEvent>());
        eventGroups.put(EventType.EntityDeath, new EventGroup<EntityDeathEvent>());
        eventGroups.put(EventType.PlayerDeath, new EventGroup<PlayerDeathEvent>());
        eventGroups.put(EventType.BlockReplaceEvent, new EventGroup<BlockReplaceEvent>());
        eventGroups.put(EventType.TickUpdate, new EventGroup<WorldTickUpdateEvent>());
        eventGroups.put(EventType.KeyInput, new EventGroup<KeyInputEvent>());
        eventGroups.put(EventType.ButtonInput, new EventGroup<ButtonInputEvent>());
        Log.print(TAG, "事件总线初始化完成！");
    }

    public void initialize() {
    }

    /**
     * 添加事件
     * @param eventType
     * @param event
     * @return
     */
    public <T extends Event> boolean addEvent (EventType eventType, T event) {
        if (!eventGroups.containsKey(eventType)) {
            throw new IllegalArgumentException("不存在的事件类型：" + eventType);
        }
        return eventGroups.get(eventType).addEvent(event);
    }

    /**
     * 获取事件组
     * */
    public EventGroup<? extends Event> getEventGroup (EventType eventType) {
        if (this.eventGroups.containsKey(eventType)) {
            return this.eventGroups.get(eventType);
        }
        Log.error(TAG, "EventType：" + eventType.name() + " 不存在！！！");
        throw new IllegalArgumentException("不存在的事件类型：" + eventType);
    }

    /**
     * 执行事件
     * */
    public void callEvent (EventType eventType, Object... args) {
        EventGroup<? extends Event> eventGroup = getEventGroup(eventType);
        HashSet<? extends Event> events = eventGroup.getEvents();
        for (Event event : events) {
            event.call(args);
        }
    }


    public static EventBus getInstance () {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }
}

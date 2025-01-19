package ttk.muxiuesd.world.event;

import ttk.muxiuesd.interfaces.Event;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.event.abs.BulletShootEvent;
import ttk.muxiuesd.world.event.abs.EntityAttackedEvent;
import ttk.muxiuesd.world.event.abs.EntityDeathEvent;

import java.util.HashMap;

/**
 * 事件总线
 * */
public class EventBus {
    public final String TAG = this.getClass().getName();

    //事件组id
    public static final int BulletShoot = 0;
    public static final int EntityAttacked = 1;
    public static final int EntityDeath = 2;

    //单例模式，游戏里的唯一事件总线
    private static EventBus instance;
    private final HashMap<Integer, EventGroup> eventGroups;

    private EventBus() {
        eventGroups = new HashMap<>();
        eventGroups.put(BulletShoot, new EventGroup<BulletShootEvent>());
        eventGroups.put(EntityAttacked, new EventGroup<EntityAttackedEvent>());
        eventGroups.put(EntityDeath, new EventGroup<EntityDeathEvent>());
    }

    public void initialize() {
    }

    /**
     * 添加事件
     * @param eventGroupId
     * @param event
     * @return
     */
    public <T extends Event> boolean addEvent (int eventGroupId, T event) {
        if (eventGroups.containsKey(eventGroupId)) {
            return eventGroups.get(eventGroupId).addEvent(event);
        }
        return false;
    }

    /**
     * 获取事件组
     * */
    public EventGroup getEventGroup (int eventGroupId) {
        if (this.eventGroups.containsKey(eventGroupId)) {
            return this.eventGroups.get(eventGroupId);
        }
        Log.error(TAG, "eventGroupId：" + eventGroupId+ " 不存在！！！");
        return null;
    }

    public static EventBus getInstance () {
        if (instance == null) {
            instance = new EventBus();
        }
        return instance;
    }
}

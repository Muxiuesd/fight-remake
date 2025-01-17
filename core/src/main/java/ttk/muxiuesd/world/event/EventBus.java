package ttk.muxiuesd.world.event;

import ttk.muxiuesd.interfaces.Event;
import ttk.muxiuesd.util.Log;

import java.util.HashMap;

/**
 * 事件总线
 * */
public class EventBus {
    public final String TAG = this.getClass().getName();

    //事件组id
    public static final int BulletShoot = 0;
    public static final int EntityAttacked = 1;

    //单例模式，游戏里的唯一事件总线
    private static EventBus instance;
    private HashMap<Integer, EventGroup> eventGroups;

    private EventBus() {
        eventGroups = new HashMap<>();
        eventGroups.put(BulletShoot, new EventGroup<BulletShootEvent>());
        eventGroups.put(EntityAttacked, new EventGroup<EntityAttackedEvent>());
    }

    public void initialize() {
        //添加Mod的事件调用

    }

    public boolean addEvent (int eventGroupId, Event event) {
        if (eventGroups.containsKey(eventGroupId)) {
            return eventGroups.get(eventGroupId).addEvent(event);
        }
        return false;
    }

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

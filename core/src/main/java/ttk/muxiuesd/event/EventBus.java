package ttk.muxiuesd.event;

import ttk.muxiuesd.util.Log;

import java.util.concurrent.ConcurrentHashMap;

public class EventBus {
    public static final String TAG = EventBus.class.getName();
    private static final ConcurrentHashMap<String, EventHandler> eventHandlersTable = new ConcurrentHashMap<>();

    /**
     * 注册一种事件类型
     * */
    public static <T extends Event, P extends EventPoster> String register (String eventType, EventHandler<T, P> eventHandler) {
        if (eventHandlersTable.containsKey(eventType)) {
            Log.error(TAG, "事件类型：" + eventType + " 已被注册过，执行覆盖！！！");
        }
        eventHandlersTable.put(eventType, eventHandler);
        return eventType;
    }

    public static void unregister (String eventType) {
        if (! eventHandlersTable.containsKey(eventType)) {
            Log.error(TAG, "事件类型：" + eventType + " 不存在，无法取消注册！！！");
            throw new IllegalArgumentException(eventType);
        }
        eventHandlersTable.remove(eventType);
    }

    /**
     * 发送事件
     * */
    public static <P extends EventPoster> void post (String eventType, P eventPoster) {
        if (! eventHandlersTable.containsKey(eventType)) {
            Log.error(TAG, "事件类型：" + eventType + " 不存在，无法发送事件！！！");
            throw new IllegalArgumentException(eventType);
        }
        eventHandlersTable.get(eventType).callEvents(eventPoster);
    }

    /**
     * 订阅事件
     * */
    public static <T extends Event> void subscribe (String eventType, T event) {
        if (! eventHandlersTable.containsKey(eventType)) {
            Log.error(TAG, "事件类型：" + eventType + " 不存在，无法订阅！！！");
            throw new IllegalArgumentException(eventType);
        }
        eventHandlersTable.get(eventType).addEvent(event);
    }
}

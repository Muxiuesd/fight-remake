package ttk.muxiuesd.event;

import game.muxiuesd.bedrockcore.util.Log;

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

    /**
     * 取消订阅事件（按事件实例，引用匹配）
     * <p>
     * 世界销毁时应取消其订阅的事件实例，防止全局事件总线累积重复订阅导致事件多次触发
     * */
    public static <T extends Event> void unsubscribe (String eventType, T event) {
        if (! eventHandlersTable.containsKey(eventType)) {
            Log.error(TAG, "事件类型：" + eventType + " 不存在，无法取消订阅！！！");
            return;
        }
        eventHandlersTable.get(eventType).removeEvent(event);
    }
}

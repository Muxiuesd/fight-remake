package ttk.muxiuesd.event;

import java.util.HashSet;

/**
 * 事件处理组，同一种事件放在一起
 * */
public abstract class EventHandler<T extends Event, P extends EventPoster> {
    private final HashSet<T> events = new HashSet<>();;

    /**
     * 实现调用事件的逻辑
     * */
    public abstract void callEvents (P poster);

    public boolean addEvent (T event) {
        if (!this.events.contains(event)) {
            this.events.add(event);
            return true;
        }
        return false;
    }

    public boolean removeEvent (T event) {
        if (this.events.contains(event)) {
            this.events.remove(event);
            return true;
        }
        return false;
    }

    public HashSet<T> getEvents() {
        return this.events;
    }
}

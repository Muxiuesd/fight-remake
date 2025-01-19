package ttk.muxiuesd.world.event;

import java.util.HashSet;

/**
 * 事件组，同种类型的事件放在一组里
 * */
public class EventGroup<T> {
    private final HashSet<T> events;

    public EventGroup () {
        this.events = new HashSet<>();
    }

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

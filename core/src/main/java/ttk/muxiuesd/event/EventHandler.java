package ttk.muxiuesd.event;

/**
 * 事件处理器
 * */
public interface EventHandler<T extends Event> {
    void handle(T event);
}

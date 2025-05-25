package ttk.muxiuesd.interfaces;

/**
 * 属性
 * */
public interface Properties {
    <T> Properties add(PropertyType<T> type, T value);
    Properties remove();

    <T> PropertyType<T> get();

}

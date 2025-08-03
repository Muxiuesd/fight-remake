package ttk.muxiuesd.interfaces.util;

/**
 * 一对键值对
 * */
public interface Pair<K, T> {
    void set(K key, T value);
    K getKey ();
    T getValue();
    void setKey(K key);
    void setValue(T value);
}

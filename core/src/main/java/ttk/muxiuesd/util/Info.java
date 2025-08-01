package ttk.muxiuesd.util;

import ttk.muxiuesd.interfaces.util.StringPair;

/**
 * 信息类
 * */
public class Info<V> implements StringPair<V> {
    private String key;
    private V value;


    public static <V> Info<V> create(String key, V defaultValue) {
        if ((key == null) || (defaultValue == null)) return null;
        return new Info<V>(key, defaultValue);
    }

    private Info(String key, V value) {
        this.set(key, value);
    }

    @Override
    public void set (String key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey () {
        return this.key;
    }

    @Override
    public V getValue () {
        return this.value;
    }

    @Override
    public void setKey (String key) {
        if (key == null) return;
        this.key = key;
    }

    @Override
    public void setValue (V value) {
        if (value == null) return;
        this.value = value;
    }
}

package ttk.muxiuesd.serialization.abs;

import ttk.muxiuesd.interfaces.data.DataReader;
import ttk.muxiuesd.interfaces.data.DataWriter;

import java.util.HashMap;

/**
 * 可编码的HashMap
 * */
public abstract class HashMapCodec <T, K, V, W extends DataWriter<?>, R extends DataReader<?>> extends HashMap<K, V> {
    /**
     * 编码
     * */
    public abstract void encode (W writer);
    /**
     * 解码
     * */
    public abstract void decode (R reader);

    public void putIfNull (K key, V value) {
        if (containsKey(key)) return;
        put(key, value);
    }
}

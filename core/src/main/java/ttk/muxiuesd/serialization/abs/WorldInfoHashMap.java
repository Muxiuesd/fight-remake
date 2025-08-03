package ttk.muxiuesd.serialization.abs;

import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.util.Info;

public abstract class WorldInfoHashMap<T, V> extends HashMapCodec<T, String, V, JsonDataWriter, JsonDataReader>{
    private String id;

    public String getId () {
        return id;
    }

    public WorldInfoHashMap<T, V> setId (String id) {
        this.id = id;
        return this;
    }

    /**
     * 如果不存在这个信息，就加进来
     * */
    public void putIfNull (Info<V> pair) {
        if (containsKey(pair.getKey())) return;
        put(pair.getKey(), pair.getValue());
    }

    public V get (Info<V> pair) {
        if (containsKey(pair.getKey())) return get(pair.getKey());
        return pair.getValue();
    }
}

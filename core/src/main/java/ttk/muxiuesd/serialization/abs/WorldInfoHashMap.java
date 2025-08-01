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

    public void putIfNull (Info<V> pair) {
        if (containsKey(pair.getKey())) return;
        put(pair.getKey(), pair.getValue());
    }
}

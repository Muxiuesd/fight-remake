package ttk.muxiuesd.serialization.abs;

import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;

public abstract class WorldInfoHashMap<T, V> extends HashMapCodec<T, String, V, JsonDataWriter, JsonDataReader>{
    private String id;

    public String getId () {
        return id;
    }

    public WorldInfoHashMap<T, V> setId (String id) {
        this.id = id;
        return this;
    }
}

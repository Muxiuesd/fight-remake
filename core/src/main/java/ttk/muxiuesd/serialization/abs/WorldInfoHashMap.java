package ttk.muxiuesd.serialization.abs;

import game.muxiuesd.bedrockcore.data.JsonDataReader;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.util.Info;

/**
 * 世界信息的哈希表
 * */
public abstract class WorldInfoHashMap<T, V> extends HashMapCodec<T, String, V, JsonDataWriter, JsonDataReader>{
    private Identifier identifier;

    public String getId () {
        return this.getIdentifier() == null ? null : this.getIdentifier().getID();
    }

    public Identifier getIdentifier () {
        return this.identifier;
    }

    public WorldInfoHashMap<T, V> setIdentifier (Identifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public WorldInfoHashMap<T, V> setId (String id) {
        //总是创建新实例，防止修改共享的注册表 key（Identifier 的 hashCode 基于 id）
        this.identifier = new Identifier(id);
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

package ttk.muxiuesd.serialization.abs;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.util.Info;

import java.util.HashMap;

/**
 * 世界信息的哈希表
 * */
public abstract class WorldInfoHashMap<T, V> extends HashMap<String, V>{
    private Identifier identifier;

    public String getId () {
        return this.getIdentifier() == null ? null : this.getIdentifier().getID();
    }

    public Identifier getIdentifier () {
        return this.identifier;
    }

    public WorldInfoHashMap<T, V> setIdentifier (Identifier identifier) {
        //Identifier 只在注册阶段给定，注册过后不允许修改
        if (this.identifier != null && !this.identifier.equals(identifier)) {
            throw new IllegalStateException("Identifier 已设置，禁止修改！世界信息：" + this.identifier.getID() + " -> " + identifier.getID());
        }
        this.identifier = identifier;
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

package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.serialization.abs.WorldInfoHashMap;
import ttk.muxiuesd.serialization.hashmap.FloatHashMapCodec;
import ttk.muxiuesd.serialization.hashmap.IntHashMapCodec;
import ttk.muxiuesd.serialization.hashmap.LongHashMapCodec;
import ttk.muxiuesd.serialization.hashmap.StringHashMapCodec;

/**
 * 世界信息类型注册表
 * */
public final class WorldInfoTypes {
    public static void init () {}

    public static final WorldInfoHashMap<IntHashMapCodec, Integer> INT = register("int", new IntHashMapCodec());
    public static final WorldInfoHashMap<LongHashMapCodec, Long> LONG = register("long", new LongHashMapCodec());
    public static final WorldInfoHashMap<FloatHashMapCodec, Float> FLOAT = register("float", new FloatHashMapCodec());
    public static final WorldInfoHashMap<StringHashMapCodec, String> STRING = register("string", new StringHashMapCodec());

    /**
     * 注册一种信息类型的hashmap
     * */
    public static <T, V> WorldInfoHashMap<T, V> register (String name, WorldInfoHashMap<T, V> map) {
        Identifier identifier = Identifier.of(Fight.ID(name));
        map.setIdentifier(identifier);
        Registries.WORLD_INFO_HASH_MAP.register(identifier, map);
        return map;
    }
}

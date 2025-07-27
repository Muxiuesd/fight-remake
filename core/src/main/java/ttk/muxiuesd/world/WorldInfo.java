package ttk.muxiuesd.world;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.serialization.IntHashMapCodec;
import ttk.muxiuesd.serialization.LongHashMapCodec;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.serialization.abs.WorldInfoHashMap;

import java.util.HashMap;
import java.util.Optional;

/**
 * 世界信息类，存储一些需要跟随存档读写的数据
 * */
public class WorldInfo {
    public static final WorldInfoHashMap<IntHashMapCodec, Integer> INT = register("int", new IntHashMapCodec());
    public static final WorldInfoHashMap<LongHashMapCodec, Long> LONG = register("long", new LongHashMapCodec());

    public static <T, V> WorldInfoHashMap<T, V> register (String name, WorldInfoHashMap<T, V> map) {
        String id = Fight.getId(name);
        map.setId(id);
        Registries.WORLD_INFO_HASH_MAP.register(new Identifier(id), map);
        return map;
    }


    public static final Codec CODEC = new Codec();
    public static WorldInfo INSTANCE;
    public static String FILE_NAME = "worldInfo.json";


    private final HashMap<String, WorldInfoHashMap<?, ?>> information;

    public WorldInfo () {
        this.information = new HashMap<>();
        this.addInfoType("int", INT);
        this.addInfoType("long", LONG);

        this.setInfo("int", "seed", 114514);
        this.setInfo("int", "abc", 12138);
    }

    /**
     * 添加一类信息
     * */
    public void addInfoType (String typeName, WorldInfoHashMap<?, ?> map) {
        if (this.information.containsKey(typeName) || this.information.containsValue(map)) return;
        this.information.put(typeName, map);
    }

    /**
     * 设置一种信息
     * */
    public <T> void setInfo (String typeName, String key, T value) {
        if (!this.information.containsKey(typeName)) {
            return;
        }
        WorldInfoHashMap<?, T> map = (WorldInfoHashMap<?, T>) this.information.get(typeName);

        map.put(key, value);
    }

    public <T> T getInfo (String typeName, String key) {
        if (!this.information.containsKey(typeName)) {
            return null;
        }
        return (T) this.information.get(typeName).get(key);
    }

    /**
    * 编解码器
    * */
    public static class Codec extends JsonCodec<WorldInfo> {
        @Override
        public void encode (WorldInfo obj, JsonDataWriter dataWriter) {
            for (WorldInfoHashMap mapCodec : obj.information.values()) {
                dataWriter.objStart(mapCodec.getId());
                mapCodec.encode(dataWriter);
                dataWriter.objEnd();
            }
        }
        @Override
        protected Optional<WorldInfo> parse (JsonDataReader dataReader) {
            WorldInfo worldInfo = new WorldInfo();
            //TODO
            dataReader.getParse().forEach((obj -> {
                String name = obj.name();
                //从注册表里面读取
                WorldInfoHashMap<?, ?> map = Registries.WORLD_INFO_HASH_MAP.get(name);
                map.decode(new JsonDataReader(obj));
                worldInfo.addInfoType(name, map);
            }));

            return Optional.of(worldInfo);
        }
    }
}

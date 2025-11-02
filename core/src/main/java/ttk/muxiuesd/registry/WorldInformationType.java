package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.serialization.abs.WorldInfoHashMap;
import ttk.muxiuesd.serialization.hashmap.FloatHashMapCodec;
import ttk.muxiuesd.serialization.hashmap.IntHashMapCodec;
import ttk.muxiuesd.serialization.hashmap.LongHashMapCodec;
import ttk.muxiuesd.util.FileUtil;
import ttk.muxiuesd.world.WorldInfo;

import java.util.Optional;

/**
 * 世界信息类型注册表
 * */
public final class WorldInformationType {
    public static void init () {}

    public static final WorldInfoHashMap<IntHashMapCodec, Integer> INT = register("int", new IntHashMapCodec());
    public static final WorldInfoHashMap<LongHashMapCodec, Long> LONG = register("long", new LongHashMapCodec());
    public static final WorldInfoHashMap<FloatHashMapCodec, Float> FLOAT = register("float", new FloatHashMapCodec());

    static {
        //检查世界信息文件是否存在
        if(FileUtil.fileExists(Fight.PATH_SAVE, WorldInfo.FILE_NAME)) {
            //存在就读取
            String file = FileUtil.readFileAsString(Fight.PATH_SAVE, WorldInfo.FILE_NAME);
            Optional<WorldInfo> optional = WorldInfo.CODEC.parse(new JsonDataReader(file));
            if (optional.isPresent()) {
                //让这个实例存在
                WorldInfo.INSTANCE = optional.get();
            }
        }else {
            //新建一个
            WorldInfo.INSTANCE = new WorldInfo();
        }
    }

    /**
     * 注册一种信息类型的hashmap
     * */
    public static <T, V> WorldInfoHashMap<T, V> register (String name, WorldInfoHashMap<T, V> map) {
        String id = Fight.ID(name);
        map.setId(id);
        Registries.WORLD_INFO_HASH_MAP.register(new Identifier(id), map);
        return map;
    }
}

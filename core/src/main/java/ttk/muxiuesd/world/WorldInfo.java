package ttk.muxiuesd.world;

import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.serialization.abs.WorldInfoHashMap;

import java.util.HashMap;
import java.util.Optional;

/**
 * 世界信息类，存储一些需要跟随存档读写的数据
 * */
public class WorldInfo {
    public static final Codec CODEC = new Codec();
    public static WorldInfo INSTANCE;
    public static String FILE_NAME = "worldInfo.json";


    private final HashMap<String, WorldInfoHashMap<?, ?>> information;

    public WorldInfo () {
        this.information = new HashMap<>();
        //将注册表里的信息类型map全部加进来
        Registries.WORLD_INFO_HASH_MAP.getMap().forEach((key, value) -> {
            this.information.put(key.getId(), value);
        });
    }

    /**
     * 添加一类信息，名称即为注册名
     * */
    public void addInfoType (WorldInfoHashMap<?, ?> typeMap) {
        if (typeMap.getId() == null) {
            return;
        }
        this.addInfoType(typeMap.getId(), typeMap);
    }

    /**
     * 添加一类信息
     * */
    public void addInfoType (String infoTypeId, WorldInfoHashMap<?, ?> map) {
        if (this.information.containsKey(infoTypeId) || this.information.containsValue(map)) return;
        this.information.put(infoTypeId, map);
    }

    /**
     * 设置一种信息
     * */
    public <T> void setInfo (String infoTypeId, String key, T value) {
        if (!this.information.containsKey(infoTypeId)) {
            return;
        }
        WorldInfoHashMap<?, T> map = (WorldInfoHashMap<?, T>) this.information.get(infoTypeId);

        map.put(key, value);
    }

    public <T> T getInfo (String infoTypeId, String key) {
        if (!this.information.containsKey(infoTypeId)) {
            return null;
        }
        return (T) this.information.get(infoTypeId).get(key);
    }

    public  WorldInfoHashMap<?, ?> getInfoTypeMap (String infoTypeId) {
        if (!this.information.containsKey(infoTypeId)) return null;
        return this.information.get(infoTypeId);
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
        public Optional<WorldInfo> parse (JsonDataReader dataReader) {
            WorldInfo worldInfo = new WorldInfo();

            dataReader.getParse().forEach((obj -> {
                WorldInfoHashMap<?, ?> infoTypeMap = worldInfo.getInfoTypeMap(obj.name());
                infoTypeMap.decode(new JsonDataReader(obj));
            }));

            return Optional.of(worldInfo);
        }
    }
}

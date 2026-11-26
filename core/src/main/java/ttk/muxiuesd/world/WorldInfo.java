package ttk.muxiuesd.world;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.DataResult;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.serialization.abs.WorldInfoHashMap;
import ttk.muxiuesd.serialization.hashmap.FloatHashMapCodec;
import ttk.muxiuesd.serialization.hashmap.IntHashMapCodec;
import ttk.muxiuesd.serialization.hashmap.LongHashMapCodec;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 世界信息类
 * <p>
 * 存储一些需要跟随存档读写的数据
 * */
public class WorldInfo {
    /**
     * 世界信息的现代化编解码器（新式 RawObject 格式，键结构与旧 JSON 格式兼容）
     */
    public static final Codec<WorldInfo> CODEC = new Codec<>() {
        @Override
        public RawObject encode (WorldInfo obj) {
            Map<String, Object> map = new LinkedHashMap<>();
            obj.information.forEach((id, mapCodec) -> {
                Map<String, Object> inner = new LinkedHashMap<>();
                mapCodec.forEach((key, value) -> inner.put(key, value));
                map.put(id, inner);
            });
            return RawObject.ofMap(map);
        }

        @Override
        public DataResult<WorldInfo> decode (RawObject input) {
            if (!input.isMap()) return DataResult.error("Expected a map");
            WorldInfo worldInfo = new WorldInfo();

            input.asMap().get().forEach((infoTypeId, rawValue) -> {
                //未知的信息类型（旧存档数据/已移除的info type）：跳过，不崩溃
                WorldInfoHashMap<?, ?> infoTypeMap = worldInfo.getInfoTypeMap(infoTypeId);
                if (infoTypeMap == null) return;

                RawObject inner = game.muxiuesd.bedrockcore.serialization.Codec.wrap(rawValue);
                if (!inner.isMap()) return;
                inner.asMap().get().forEach((key, value) -> {
                    ((Map<String, Object>) infoTypeMap).put(key, convertValue(infoTypeMap, value));
                });
            });

            return DataResult.success(worldInfo);
        }

        /**
         * 根据 map 的类型把 JSON 解析出的数值转换到正确的类型
         */
        private Object convertValue (WorldInfoHashMap<?, ?> map, Object raw) {
            if (raw instanceof Number n) {
                if (map instanceof IntHashMapCodec) return n.intValue();
                if (map instanceof LongHashMapCodec) return n.longValue();
                if (map instanceof FloatHashMapCodec) return n.floatValue();
            }
            return raw;
        }
    };

    public static String FILE_NAME = "worldInfo.json";

    private static WorldInfo INSTANCE;

    public static WorldInfo getInstance () { return INSTANCE; }
    public static void setInstance (WorldInfo newInstance) {
        if (newInstance == null) return;
        INSTANCE = newInstance;
    }

    private final HashMap<String, WorldInfoHashMap<?, ?>> information;

    public WorldInfo () {
        this.information = new HashMap<>();
        this.addRegistryMap();
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

    public WorldInfoHashMap<?, ?> getInfoTypeMap (String infoTypeId) {
        if (!this.information.containsKey(infoTypeId)) return null;
        return this.information.get(infoTypeId);
    }

    /**
     * 重置世界信息
     * */
    public void reset () {
        Registries.WORLD_INFO_HASH_MAP.getMap().forEach((key, value) -> {
            value.clear();
        });

        this.clearAllInfoMap();
        this.addRegistryMap();
    }

    /**
     * 把当前注册表里面注册的信息类型map给添加进来
     * */
    public void addRegistryMap () {
        Registries.WORLD_INFO_HASH_MAP.getMap().forEach((key, value) -> {
            this.information.put(key.getID(), value);
        });
    }

    /**
     * 清理所有的世界信息表
     * */
    public void clearAllInfoMap () {
        this.information.clear();
    }
}

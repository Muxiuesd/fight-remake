package ttk.muxiuesd.serialization.codecs;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.CodecBuilder;
import game.muxiuesd.bedrockcore.serialization.DataResult;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import ttk.muxiuesd.data.JsonPropertiesMap;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.registrant.Registries;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link JsonPropertiesMap}的现代化编解码器
 * */
public class CodecJsonPropertiesMap extends Codec<JsonPropertiesMap> {
    /**
     * map的编解码器
     * */
    public static final Codec<LinkedHashMap<PropertyType, Object>> MAP_CODEC = new Codec<>() {
        @Override
        public RawObject encode (LinkedHashMap<PropertyType, Object> map) {
            Map<String, Object> encodedMap = new LinkedHashMap<>();

            for (Map.Entry<PropertyType, Object> entry : map.entrySet()) {
                //获取这个属性类型的id
                String key = entry.getKey().getId();
                //调用这个属性类型的值的编解码器
                Codec<Object> codec = (Codec<Object>) entry.getKey().getValueCodec();
                //将属性的值编码
                RawObject rawObject = codec.encode(entry.getValue());
                encodedMap.put(key, rawObject.unwrap());
            }

            return RawObject.ofMap(encodedMap);
        }

        @Override
        public DataResult<LinkedHashMap<PropertyType, Object>> decode (RawObject input) {
            if (!input.isMap()) return DataResult.error("Expected a map");

            Map<String, Object> rawMap = input.asMap().get();
            LinkedHashMap<PropertyType, Object> result = new LinkedHashMap<>();
            StringBuilder errors = new StringBuilder();
            boolean hasError = false;

            for (Map.Entry<String, Object> rawEntry : rawMap.entrySet()) {
                String id = rawEntry.getKey();
                PropertyType<?> propType = Registries.PROPERTY_TYPE.get(id);
                if (propType == null) {
                    errors.append("Unknown property: ").append(id).append("; ");
                    hasError = true;
                    continue;
                }
                Codec<Object> codec = (Codec<Object>) propType.getValueCodec();
                DataResult<Object> decoded = codec.decode(Codec.wrap(rawEntry.getValue()));
                if (decoded.isSuccess()) {
                    result.put(propType, decoded.result().get());
                } else {
                    hasError = true;
                    errors.append("[").append(id).append("]: ").append(decoded.error().orElse("")).append("; ");
                    if (decoded.result().isPresent())
                        result.put(propType, decoded.result().get());
                }
            }
            if (hasError) return DataResult.error(errors.toString(), result);
            return DataResult.success(result);
        }
    };

    public static final Codec<JsonPropertiesMap> CODEC = CodecBuilder.create(JsonPropertiesMap::new)
        .field("properties_map", JsonPropertiesMap::getPropertiesMap, JsonPropertiesMap::setPropertiesMap, MAP_CODEC)
        .build();

    @Override
    public RawObject encode (JsonPropertiesMap value) {
        return null;
    }

    @Override
    public DataResult<JsonPropertiesMap> decode (RawObject input) {
        return null;
    }
}

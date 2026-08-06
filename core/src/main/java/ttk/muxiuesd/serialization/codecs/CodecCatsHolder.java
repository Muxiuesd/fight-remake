package ttk.muxiuesd.serialization.codecs;

import com.badlogic.gdx.utils.JsonValue;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.DataResult;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import ttk.muxiuesd.world.cat.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link CatsHolder}的现代化编解码器
 * <p>
 * cats的数据是key-value的形式，值的类型通过json值的类型推断
 * */
public class CodecCatsHolder {
    public static final Codec<CatsHolder> CODEC = new Codec<>() {
        @Override
        public RawObject encode (CatsHolder holder) {
            Map<String, Object> map = new LinkedHashMap<>();
            holder.getMap().forEach((key, catValue) -> {
                map.put(key, catValue.get());
            });
            return RawObject.ofMap(map);
        }

        @Override
        public DataResult<CatsHolder> decode (RawObject input) {
            if (!input.isMap()) return DataResult.error("Expected a map");
            CatsHolder holder = new CatsHolder();
            StringBuilder errors = new StringBuilder();
            for (Map.Entry<String, Object> entry : input.asMap().get().entrySet()) {
                CatValue<?> catValue = createCatValue(entry.getValue());
                if (catValue != null) {
                    holder.put(entry.getKey(), catValue);
                } else {
                    errors.append(entry.getKey()).append(": 未知的cat值类型; ");
                }
            }
            if (errors.length() > 0) return DataResult.error(errors.toString(), holder);
            return DataResult.success(holder);
        }
    };

    /**
     * 根据值的类型推断对应的cat值包装类
     * */
    private static CatValue<?> createCatValue (Object raw) {
        if (raw instanceof Integer i) return new CatInt(i);
        if (raw instanceof Long l) return new CatLong(l);
        if (raw instanceof Boolean b) return new CatBoolean(b);
        if (raw instanceof Float f) return new CatFloat(f);
        if (raw instanceof Double d) return new CatFloat(d.floatValue());
        if (raw instanceof String s) return new CatString(s);
        return null;
    }

    /**
     * 将cats持有者转换为json值树
     * <p>
     * 用于适配旧的{@code readCatData(JsonValue)}接口
     * */
    public static JsonValue toJsonValue (CatsHolder holder) {
        JsonValue json = new JsonValue(JsonValue.ValueType.object);
        holder.getMap().forEach((key, catValue) -> {
            json.addChild(key, toJsonValue(catValue));
        });
        return json;
    }

    private static JsonValue toJsonValue (CatValue<?> catValue) {
        Object value = catValue.get();
        if (value instanceof Integer i) return new JsonValue(i);
        if (value instanceof Long l) return new JsonValue(l);
        if (value instanceof Boolean b) return new JsonValue(b);
        if (value instanceof Float f) return new JsonValue(f);
        if (value instanceof Double d) return new JsonValue(d);
        return new JsonValue(String.valueOf(value));
    }
}

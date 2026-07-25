package game.muxiuesd.bedrockcore.serialization;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 编解码器输出的原始对象（一堆数据的集合）
 * */
public class RawObject {
    private final Object value;

    private RawObject(Object value) {
        this.value = value;
    }

    /// 工厂方法
    public static RawObject ofMap (Map<String, Object> map) { return new RawObject(map); }

    public static RawObject ofList (List<Object> list) { return new RawObject(list); }

    public static RawObject ofString (String s) { return new RawObject(s); }

    public static RawObject ofInt (int i) { return new RawObject(i); }

    public static RawObject ofBoolean (boolean b) { return new RawObject(b); }

    public static RawObject ofNull () { return new RawObject(null); }

    /// 类型检查
    public boolean isMap() { return value instanceof Map; }

    public boolean isList() { return value instanceof List; }

    public boolean isString() { return value instanceof String; }

    public boolean isInt() { return value instanceof Integer; }

    public boolean isBoolean() { return value instanceof Boolean; }

    public boolean isNull() { return value == null; }

    @SuppressWarnings("unchecked")
    public Optional<Map<String, Object>> asMap() {
        return (value instanceof Map) ? Optional.of((Map<String, Object>) value) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public Optional<List<Object>> asList() {
        return (value instanceof List) ? Optional.of((List<Object>) value) : Optional.empty();
    }

    public Optional<String> asString() {
        return (value instanceof String) ? Optional.of((String) value) : Optional.empty();
    }

    public Optional<Integer> asInt() {
        return (value instanceof Integer) ? Optional.of((Integer) value) : Optional.empty();
    }

    public Optional<Boolean> asBoolean() {
        return (value instanceof Boolean) ? Optional.of((Boolean) value) : Optional.empty();
    }

    public Object unwrap() { return value; }

    @Override
    public String toString() { return String.valueOf(value); }
}

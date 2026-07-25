package game.muxiuesd.bedrockcore.serialization;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 编解码器
 * */
public abstract class Codec<T> {

    public abstract RawObject encode(T value);

    public abstract DataResult<T> decode(RawObject input);

    public <R> Codec<R> xmap (Function<T, R> to, Function<R, T> from) {
        Codec<T> self = this;
        return new Codec<R>() {
            public RawObject encode(R value) {
                return self.encode(from.apply(value));
            }
            public DataResult<R> decode(RawObject input) {
                return self.decode(input).map(to);
            }
        };
    }

    /// 基础类型的编解码器实现

    public static final Codec<Integer> INT = new Codec<Integer>() {
        public RawObject encode(Integer value) { return RawObject.ofInt(value); }
        public DataResult<Integer> decode(RawObject input) {
            if (input.isInt()) return DataResult.success(input.asInt().get());
            if (input.isString()) {
                try { return DataResult.success(Integer.parseInt(input.asString().get())); }
                catch (NumberFormatException e) { return DataResult.error("Not an int"); }
            }
            return DataResult.error("Not an int");
        }
    };

    public static final Codec<String> STRING = new Codec<String>() {
        public RawObject encode(String value) { return RawObject.ofString(value); }
        public DataResult<String> decode(RawObject input) {
            return input.isString() ? DataResult.success(input.asString().get()) : DataResult.error("Not a string");
        }
    };

    public static final Codec<Boolean> BOOL = new Codec<Boolean>() {
        public RawObject encode(Boolean value) { return RawObject.ofBoolean(value); }
        public DataResult<Boolean> decode(RawObject input) {
            return input.isBoolean() ? DataResult.success(input.asBoolean().get()) : DataResult.error("Not a boolean");
        }
    };

    public static <T> Codec<List<T>> listOf (Codec<T> elementCodec) {
        return new Codec<>() {
            public RawObject encode (List<T> list) {
                List<Object> encoded = new ArrayList<>();
                for (T elem : list) encoded.add(elementCodec.encode(elem).unwrap());
                return RawObject.ofList(encoded);
            }

            public DataResult<List<T>> decode (RawObject input) {
                if (! input.isList()) return DataResult.error("Not a list");
                List<?> rawList = input.asList().get();
                List<T> result = new ArrayList<>();
                StringBuilder errors = new StringBuilder();
                boolean hasError = false;
                for (int i = 0; i < rawList.size(); i++) {
                    DataResult<T> decoded = elementCodec.decode(wrap(rawList.get(i)));
                    if (decoded.isSuccess()) result.add(((DataResult.Success<T>) decoded).value);
                    else {
                        hasError = true;
                        errors.append("[").append(i).append("]: ").append(decoded.error().orElse("")).append("; ");
                        result.add(decoded.result().orElse(null));
                    }
                }
                if (hasError) return DataResult.error(errors.toString(), result);
                return DataResult.success(result);
            }
        };
    }

    public static <V> Codec<Map<String, V>> mapOf (Codec<V> valueCodec) {
        return new Codec<>() {
            public RawObject encode (Map<String, V> map) {
                Map<String, Object> encoded = new LinkedHashMap<>();
                for (Map.Entry<String, V> e : map.entrySet())
                    encoded.put(e.getKey(), valueCodec.encode(e.getValue()).unwrap());
                return RawObject.ofMap(encoded);
            }

            public DataResult<Map<String, V>> decode (RawObject input) {
                if (! input.isMap()) return DataResult.error("Not a map");
                Map<String, Object> rawMap = input.asMap().get();
                Map<String, V> result = new LinkedHashMap<>();
                StringBuilder errors = new StringBuilder();
                boolean hasError = false;
                for (Map.Entry<String, Object> e : rawMap.entrySet()) {
                    DataResult<V> decoded = valueCodec.decode(wrap(e.getValue()));
                    if (decoded.isSuccess()) result.put(e.getKey(), ((DataResult.Success<V>) decoded).value);
                    else {
                        hasError = true;
                        errors.append("[").append(e.getKey()).append("]: ").append(decoded.error().orElse("")).append("; ");
                        if (decoded.result().isPresent()) result.put(e.getKey(), decoded.result().get());
                    }
                }
                if (hasError) return DataResult.error(errors.toString(), result);
                return DataResult.success(result);
            }
        };
    }

    @SuppressWarnings("unchecked")
    public static RawObject wrap(Object val) {
        if (val instanceof RawObject) return (RawObject) val;
        if (val instanceof Map) return RawObject.ofMap((Map<String, Object>) val);
        if (val instanceof List) return RawObject.ofList((List<Object>) val);
        if (val instanceof String) return RawObject.ofString((String) val);
        if (val instanceof Integer) return RawObject.ofInt((Integer) val);
        if (val instanceof Boolean) return RawObject.ofBoolean((Boolean) val);
        if (val == null) return RawObject.ofNull();
        return RawObject.ofString(val.toString());
    }
}

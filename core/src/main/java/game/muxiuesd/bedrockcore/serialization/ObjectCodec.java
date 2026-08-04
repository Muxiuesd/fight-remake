package game.muxiuesd.bedrockcore.serialization;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;


/**
 * 一个具体的对象的现代化编解码器
 * */
public class ObjectCodec<T> extends Codec<T> {
    private final Function<Object[], T> factory;
    private final List<ParamField<T, ?>> paramFields;
    private final List<FieldBinding<T, ?>> setterFields;

    public ObjectCodec(Function<Object[], T> factory,
                List<ParamField<T, ?>> paramFields,
                List<FieldBinding<T, ?>> setterFields) {
        this.factory = factory;
        this.paramFields = paramFields;
        this.setterFields = setterFields;
    }

    @Override @SuppressWarnings("unchecked")
    public RawObject encode(T value) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (ParamField<T, ?> p : paramFields) {
            Object fv = ((Function<T, Object>) p.getter).apply(value);
            map.put(p.name, ((Codec<Object>) p.codec).encode(fv).unwrap());
        }
        for (FieldBinding<T, ?> f : setterFields) {
            if (!f.decodeOnRead) continue;
            Object fv = ((Function<T, Object>) f.getter).apply(value);
            map.put(f.name, ((Codec<Object>) f.codec).encode(fv).unwrap());
        }
        return RawObject.ofMap(map);
    }

    @Override @SuppressWarnings("unchecked")
    public DataResult<T> decode(RawObject input) {
        if (!input.isMap()) return DataResult.error("Expected a Map");
        Map<String, Object> rawMap = input.asMap().get();
        StringBuilder errors = new StringBuilder();
        boolean hasError = false;

        // 收集构造参数
        Object[] paramValues = new Object[paramFields.size()];
        for (int i = 0; i < paramFields.size(); i++) {
            ParamField<T, ?> p = paramFields.get(i);
            Object rawVal = rawMap.get(p.name);
            if (rawVal == null) {
                errors.append(p.name).append(": Missing; ");
                hasError = true;
                paramValues[i] = null;
            } else {
                DataResult<?> dr = p.codec.decode(Codec.wrap(rawVal));
                if (dr.isSuccess()) {
                    paramValues[i] = ((DataResult.Success<?>) dr).value;
                } else {
                    hasError = true;
                    errors.append(p.name).append(": ").append(dr.error().orElse("")).append("; ");
                    paramValues[i] = dr.result().orElse(null);
                }
            }
        }

        T instance = factory.apply(paramValues);

        // setter 注入
        for (FieldBinding<T, ?> f : setterFields) {
            if (!f.decodeOnRead) continue;
            Object rawVal = rawMap.get(f.name);
            if (rawVal == null) {
                errors.append(f.name).append(": Missing; ");
                hasError = true;
                continue;
            }
            DataResult<?> dr = f.codec.decode(Codec.wrap(rawVal));
            if (dr.isSuccess()) {
                ((BiConsumer<T, Object>) f.setter).accept(instance, ((DataResult.Success<?>) dr).value);
            } else {
                hasError = true;
                errors.append(f.name).append(": ").append(dr.error().orElse("")).append("; ");
                if (dr.result().isPresent())
                    ((BiConsumer<T, Object>) f.setter).accept(instance, dr.result().get());
            }
        }

        if (hasError) return DataResult.error(errors.toString(), instance);
        return DataResult.success(instance);
    }





}

package game.muxiuesd.bedrockcore.serialization;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 编解码器的构造器
 * */
public class CodecBuilder<T> {
    private final Supplier<T> factory;
    private final List<FieldBinding<T, ?>> fields = new ArrayList<>();

    private CodecBuilder(Supplier<T> factory) {
        this.factory = factory;
    }

    /**
     * 新建一个编解码器的构造器
     * */
    public static <T> CodecBuilder<T> of(Supplier<T> factory) {
        return new CodecBuilder<>(factory);
    }

    /**
     * 设定被编解码的字段
     * @param name      字段名
     * @param getter    获取字段值的方法
     * @param setter    设置字段值的方法
     * @param codec     此字段的编解码器
     * */
    public <F> CodecBuilder<T> field (String name, Function<T, F> getter, BiConsumer<T, F> setter, Codec<F> codec) {
        fields.add(new FieldBinding<>(name, getter, setter, codec));
        return this;
    }

    public Codec<T> build() {
        return new ObjectCodec<>(factory, new ArrayList<>(fields));
    }

    /**
     * 字段绑定
     * */
    public static class FieldBinding<T, F> {
        private final String name;
        private final Function<T, F> getter;
        private final BiConsumer<T, F> setter;
        private final Codec<F> codec;
        FieldBinding(String name, Function<T, F> getter, BiConsumer<T, F> setter, Codec<F> codec) {
            this.name = name; this.getter = getter; this.setter = setter; this.codec = codec;
        }
    }

    /**
     * 对象的编解码器
     * */
    public static class ObjectCodec<T> extends Codec<T> {
        private final Supplier<T> factory;
        private final List<FieldBinding<T, ?>> fields;

        ObjectCodec(Supplier<T> factory, List<FieldBinding<T, ?>> fields) {
            this.factory = factory; this.fields = fields;
        }

        @Override @SuppressWarnings("unchecked")
        public RawObject encode(T value) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (FieldBinding<T, ?> b : fields) {
                Object fv = ((Function<T, Object>) b.getter).apply(value);
                map.put(b.name, ((Codec<Object>) b.codec).encode(fv).unwrap());
            }
            return RawObject.ofMap(map);
        }

        @Override @SuppressWarnings("unchecked")
        public DataResult<T> decode(RawObject input) {
            if (!input.isMap()) return DataResult.error("Expected a Map");
            Map<String, Object> rawMap = input.asMap().get();
            T instance = factory.get();
            StringBuilder errors = new StringBuilder();
            boolean hasError = false;
            for (FieldBinding<T, ?> b : fields) {
                Object rawVal = rawMap.get(b.name);
                if (rawVal == null) {
                    errors.append(b.name).append(": Missing; "); hasError = true; continue;
                }
                DataResult<?> dr = b.codec.decode(Codec.wrap(rawVal));
                if (dr.isSuccess()) {
                    ((BiConsumer<T, Object>) b.setter).accept(instance, ((DataResult.Success<?>)dr).value);
                } else {
                    hasError = true;
                    errors.append(b.name).append(": ").append(dr.error().orElse("")).append("; ");
                    if (dr.result().isPresent())
                        ((BiConsumer<T, Object>) b.setter).accept(instance, dr.result().get());
                }
            }
            if (hasError) return DataResult.error(errors.toString(), instance);
            return DataResult.success(instance);
        }
    }
}

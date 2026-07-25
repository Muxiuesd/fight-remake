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

    private CodecBuilder(Supplier<T> factory) { this.factory = factory; }

    public static <T> CodecBuilder<T> of(Supplier<T> factory) { return new CodecBuilder<>(factory); }

    // 原有方法：同时编码和解码
    public <F> CodecBuilder<T> field(String name,
                                     Function<T, F> getter,
                                     BiConsumer<T, F> setter,
                                     Codec<F> codec) {
        fields.add(new FieldBinding<>(name, getter, setter, codec, true));
        return this;
    }

    // 新增方法：仅编码不解码（只读字段）
    public <F> CodecBuilder<T> encoderField(String name,
                                            Function<T, F> getter,
                                            Codec<F> codec) {
        fields.add(new FieldBinding<>(name, getter, null, codec, false));
        return this;
    }

    public Codec<T> build() { return new ObjectCodec<>(factory, new ArrayList<>(fields)); }


    // 内部字段绑定（增加标志位 decodeOnRead）
    static class FieldBinding<T, F> {
        final String name;
        final Function<T, F> getter;
        final BiConsumer<T, F> setter;   // 如果 decodeOnRead == false，则为 null
        final Codec<F> codec;
        final boolean decodeOnRead;       // 是否在解码时处理该字段

        FieldBinding(String name, Function<T, F> getter, BiConsumer<T, F> setter,
                     Codec<F> codec, boolean decodeOnRead) {
            this.name = name;
            this.getter = getter;
            this.setter = setter;
            this.codec = codec;
            this.decodeOnRead = decodeOnRead;
        }
    }

    // 对象 Codec 实现（解码时跳过 decodeOnRead == false 的字段）
    static class ObjectCodec<T> extends Codec<T> {
        private final Supplier<T> factory;
        private final List<FieldBinding<T, ?>> fields;

        ObjectCodec(Supplier<T> factory, List<FieldBinding<T, ?>> fields) {
            this.factory = factory;
            this.fields = fields;
        }

        @Override
        @SuppressWarnings("unchecked")
        public RawObject encode(T value) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (FieldBinding<T, ?> b : fields) {
                Object fv = ((Function<T, Object>) b.getter).apply(value);
                map.put(b.name, ((Codec<Object>) b.codec).encode(fv).unwrap());
            }
            return RawObject.ofMap(map);
        }

        @Override
        @SuppressWarnings("unchecked")
        public DataResult<T> decode(RawObject input) {
            if (!input.isMap()) return DataResult.error("Expected a Map");
            Map<String, Object> rawMap = input.asMap().get();
            T instance = factory.get();
            StringBuilder errors = new StringBuilder();
            boolean hasError = false;
            for (FieldBinding<T, ?> b : fields) {
                if (!b.decodeOnRead) continue;  // 跳过只读字段
                Object rawVal = rawMap.get(b.name);
                if (rawVal == null) {
                    errors.append(b.name).append(": Missing; ");
                    hasError = true;
                    continue;
                }
                DataResult<?> dr = b.codec.decode(Codec.wrap(rawVal));
                if (dr.isSuccess()) {
                    ((BiConsumer<T, Object>) b.setter).accept(instance,
                        ((DataResult.Success<?>) dr).value);
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

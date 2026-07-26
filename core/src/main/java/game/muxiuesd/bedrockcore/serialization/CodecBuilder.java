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
    private final List<ParamField<T, ?, ?>> paramFields = new ArrayList<>();
    private final List<FieldBinding<T, ?, ?>> setterFields = new ArrayList<>();
    private Function<Object[], T> constructor;

    private CodecBuilder() {}

    public static <T> CodecBuilder<T> create () {
        return new CodecBuilder<>();
    }

    public static <T> CodecBuilder<T> of (Supplier<T> supplier) {
        return new CodecBuilder<T>().noArgFactory(supplier);
    }

    // 无参工厂 (用于无构造参数的对象)
    public CodecBuilder<T> noArgFactory(Supplier<T> factory) {
        if (!paramFields.isEmpty()) throw new IllegalStateException("Cannot use noArgFactory with paramFields");
        this.constructor = args -> factory.get();
        return this;
    }

    // 构造函数参数字段
    public <F> CodecBuilder<T> paramField (String name, Function<T, F> getter, Codec<F> codec) {
        paramFields.add(new ParamField<>(name, getter, codec));
        return this;
    }

    // 工厂方法重载 (自动匹配参数个数)
    public <A> CodecBuilder<T> factory(Codec.Constructor1<A, T> factory) {
        checkParamCount(1);
        this.constructor = args -> factory.apply((A) args[0]);
        return this;
    }
    public <A, B> CodecBuilder<T> factory(Codec.Constructor2<A, B, T> factory) {
        checkParamCount(2);
        this.constructor = args -> factory.apply((A) args[0], (B) args[1]);
        return this;
    }
    public <A, B, C> CodecBuilder<T> factory(Codec.Constructor3<A, B, C, T> factory) {
        checkParamCount(3);
        this.constructor = args -> factory.apply((A) args[0], (B) args[1], (C) args[2]);
        return this;
    }
    public <A, B, C, D> CodecBuilder<T> factory(Codec.Constructor4<A, B, C, D, T> factory) {
        checkParamCount(4);
        this.constructor = args -> factory.apply((A) args[0], (B) args[1], (C) args[2], (D) args[3]);
        return this;
    }
    public <A, B, C, D, E> CodecBuilder<T> factory(Codec.Constructor5<A, B, C, D, E, T> factory) {
        checkParamCount(5);
        this.constructor = args -> factory.apply((A) args[0], (B) args[1], (C) args[2], (D) args[3], (E) args[4]);
        return this;
    }



    // 普通字段 (有 setter)
    public <F, F1 extends F> CodecBuilder<T> field(String name, Function<T, F> getter, BiConsumer<T, F> setter, Codec<F1> codec) {
        setterFields.add(new FieldBinding<>(name, getter, setter, codec, true));
        return this;
    }

    // 只编码不解码的字段
    public <F, F1 extends F> CodecBuilder<T> encoderField(String name, Function<T, F> getter, Codec<F1> codec) {
        setterFields.add(new FieldBinding<>(name, getter, null, codec, false));
        return this;
    }

    // 构建 Codec
    public Codec<T> build() {
        if (constructor == null)
            throw new IllegalStateException("No factory provided");
        return new ObjectCodec<>(constructor, paramFields, setterFields);
    }

    private void checkParamCount(int expected) {
        if (paramFields.size() != expected)
            throw new IllegalStateException("Expected " + expected + " param fields but found " + paramFields.size());
    }

    // ---------- 内部数据类 ----------
    static class ParamField<T, F, F1 extends F> {
        final String name;
        final Function<T, F> getter;
        final Codec<F1> codec;
        ParamField(String name, Function<T, F> getter, Codec<F1> codec) {
            this.name = name; this.getter = getter; this.codec = codec;
        }
    }

    static class FieldBinding<T, F, F1 extends F> {
        final String name;
        final Function<T, F> getter;
        final BiConsumer<T, F> setter;
        final Codec<F1> codec;
        final boolean decodeOnRead;

        FieldBinding(String name, Function<T, F> getter, BiConsumer<T, F> setter,
                     Codec<F1> codec, boolean decodeOnRead) {
            this.name = name; this.getter = getter; this.setter = setter;
            this.codec = codec; this.decodeOnRead = decodeOnRead;
        }
    }

    static class ObjectCodec<T> extends Codec<T> {
        private final Function<Object[], T> constructor;
        private final List<ParamField<T, ?, ?>> paramFields;
        private final List<FieldBinding<T, ?, ?>> setterFields;

        ObjectCodec(Function<Object[], T> constructor,
                    List<ParamField<T, ?, ?>> paramFields,
                    List<FieldBinding<T, ?, ?>> setterFields) {
            this.constructor = constructor;
            this.paramFields = paramFields;
            this.setterFields = setterFields;
        }

        @Override @SuppressWarnings("unchecked")
        public RawObject encode(T value) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (ParamField<T, ?, ?> p : paramFields) {
                Object fv = ((Function<T, Object>) p.getter).apply(value);
                map.put(p.name, ((Codec<Object>) p.codec).encode(fv).unwrap());
            }
            for (FieldBinding<T, ?, ?> f : setterFields) {
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
                ParamField<T, ?, ?> p = paramFields.get(i);
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

            T instance = constructor.apply(paramValues);

            // setter 注入
            for (FieldBinding<T, ?, ?> f : setterFields) {
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
}

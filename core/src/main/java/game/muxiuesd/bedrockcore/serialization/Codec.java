package game.muxiuesd.bedrockcore.serialization;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * 现代化的编解码器类
 * */
public abstract class Codec<T> {
    /**
     * 编码
     * @param value 被编码的对象
     * @return 原始对象数据
     * */
    public abstract RawObject encode (T value);

    /**
     * 编码
     * @param input 原始对象数据
     * @return 编码出来的对象结果容器
     * */
    public abstract DataResult<T> decode (RawObject input);



    /**
     * 两个类的相互转换的方法
     * @param <R> 另一种形式
     * @param to   T -> R
     * @param from R -> T
     * */
    public <R> Codec<R> xmap (Function<T, R> to, Function<R, T> from) {
        Codec<T> self = this;
        return new Codec<>() {
            @Override
            public RawObject encode (R value) {
                T t = from.apply(value);
                return self.encode(t);
            }

            @Override
            public DataResult<R> decode (RawObject input) {
                return self.decode(input).map(to);
            }
        };
    }

    /**
     * 获取这种对象的list类型的编码器
     * */
    public Codec<List<T>> listOf () {
        return listOf(this);
    }

    public Codec<Map<String,T>> mapOf () {
        return mapOf(this);
    }


    /// 基础类型的编解码器实现
    //int
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
    //string
    public static final Codec<String> STRING = new Codec<String>() {
        public RawObject encode(String value) { return RawObject.ofString(value); }
        public DataResult<String> decode(RawObject input) {
            return input.isString() ? DataResult.success(input.asString().get()) : DataResult.error("Not a string");
        }
    };

    //boolean
    public static final Codec<Boolean> BOOL = new Codec<Boolean>() {
        public RawObject encode(Boolean value) { return RawObject.ofBoolean(value); }
        public DataResult<Boolean> decode(RawObject input) {
            return input.isBoolean() ? DataResult.success(input.asBoolean().get()) : DataResult.error("Not a boolean");
        }
    };

    //long
    public static final Codec<Long> LONG = new Codec<Long>() {
        public RawObject encode(Long value) { return RawObject.ofLong(value); }
        public DataResult<Long> decode(RawObject input) {
            if (input.isLong()) return DataResult.success(input.asLong().get());
            if (input.isInt()) return DataResult.success((long) input.asInt().get());
            if (input.isString()) {
                try { return DataResult.success(Long.parseLong(input.asString().get())); }
                catch (NumberFormatException e) { return DataResult.error("Not a long"); }
            }
            return DataResult.error("Not a long");
        }
    };

    //float
    public static final Codec<Float> FLOAT = new Codec<Float>() {
        public RawObject encode(Float value) { return RawObject.ofFloat(value); }
        public DataResult<Float> decode(RawObject input) {
            if (input.isFloat()) return DataResult.success(input.asFloat().get());
            if (input.isDouble()) return DataResult.success((float) input.asDouble().get().doubleValue());
            if (input.isString()) {
                try { return DataResult.success(Float.parseFloat(input.asString().get())); }
                catch (NumberFormatException e) { return DataResult.error("Not a float"); }
            }
            return DataResult.error("Not a float");
        }
    };

    //double
    public static final Codec<Double> DOUBLE = new Codec<Double>() {
        public RawObject encode(Double value) { return RawObject.ofDouble(value); }
        public DataResult<Double> decode(RawObject input) {
            if (input.isDouble()) return DataResult.success(input.asDouble().get());
            if (input.isFloat()) return DataResult.success((double) input.asFloat().get());
            if (input.isInt()) return DataResult.success((double) input.asInt().get());
            if (input.isLong()) return DataResult.success((double) input.asLong().get());
            if (input.isString()) {
                try { return DataResult.success(Double.parseDouble(input.asString().get())); }
                catch (NumberFormatException e) { return DataResult.error("Not a double"); }
            }
            return DataResult.error("Not a double");
        }
    };

    /**
     * 构建一个类型的list编码器
     * @param <T> 对象的类型
     * @param elementCodec 被编码成list的对象的编解码器
     * @return 这个类型的list编解码器
     * */
    public static <T> Codec<List<T>> listOf (Codec<T> elementCodec) {
        return new Codec<>() {

            @Override
            public RawObject encode (List<T> list) {
                List<Object> encoded = new ArrayList<>();
                for (T elem : list) encoded.add(elementCodec.encode(elem).unwrap());
                return RawObject.ofList(encoded);
            }

            @Override
            public DataResult<List<T>> decode (RawObject input) {
                if (!input.isList()) return DataResult.error("不是一个List类型！！！");

                List<?> rawList = input.asList().get();
                List<T> resultList = new ArrayList<>();

                StringBuilder errorsStringBuilder = new StringBuilder();
                boolean hasError = false;

                for (int i = 0; i < rawList.size(); i++) {
                    //解码
                    DataResult<T> decoded = elementCodec.decode(wrap(rawList.get(i)));

                    if (decoded.isSuccess()) {
                        resultList.add(((DataResult.Success<T>) decoded).value);
                    } else {
                        hasError = true;
                        errorsStringBuilder
                            .append("[").append(i).append("]: ")
                            .append(decoded.error().orElse("")).append("; ");
                        resultList.add(decoded.result().orElse(null));
                    }
                }

                if (hasError) return DataResult.error(errorsStringBuilder.toString(), resultList);
                return DataResult.success(resultList);
            }
        };
    }

    /**
     * 对象的数组 Codec (零反射，通过 IntFunction 提供数组构造)
     * */
    public static <T> Codec<T[]> arrayOf (Codec<T> elementCodec, IntFunction<T[]> arrayBuilder) {
        return listOf(elementCodec).xmap(
            list -> list.toArray(arrayBuilder.apply(list.size())),
            Arrays::asList
        );
    }

    /**
     * 对象的MapCodec
     * */
    public static <V> Codec<Map<String, V>> mapOf(Codec<V> valueCodec) {
        return new Codec<>() {
            @Override
            public RawObject encode (Map<String, V> map) {
                Map<String, Object> encoded = new LinkedHashMap<>();
                for (Map.Entry<String, V> e : map.entrySet())
                    encoded.put(e.getKey(), valueCodec.encode(e.getValue()).unwrap());
                return RawObject.ofMap(encoded);
            }

            @Override
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
    public static RawObject wrap (Object val) {
        if (val instanceof RawObject) return (RawObject) val;
        if (val instanceof Map) return RawObject.ofMap((Map<String, Object>) val);
        if (val instanceof List) return RawObject.ofList((List<Object>) val);
        if (val instanceof String) return RawObject.ofString((String) val);
        if (val instanceof Integer) return RawObject.ofInt((Integer) val);
        if (val instanceof Boolean) return RawObject.ofBoolean((Boolean) val);
        if (val instanceof Long) return RawObject.ofLong((Long) val);
        if (val instanceof Float) return RawObject.ofFloat((Float) val);
        if (val instanceof Double) return RawObject.ofDouble((Double) val);
        if (val == null) return RawObject.ofNull();
        return RawObject.ofString(val.toString());
    }


    /**
     * 构造器函数式接口 (1~5个参数)
     * */
    @FunctionalInterface
    public interface Constructor1<A, T> { T apply(A a); }
    @FunctionalInterface
    public interface Constructor2<A, B, T> { T apply(A a, B b); }
    @FunctionalInterface
    public interface Constructor3<A, B, C, T> { T apply(A a, B b, C c); }
    @FunctionalInterface
    public interface Constructor4<A, B, C, D, T> { T apply(A a, B b, C c, D d); }
    @FunctionalInterface
    public interface Constructor5<A, B, C, D, E, T> { T apply(A a, B b, C c, D d, E e); }
}

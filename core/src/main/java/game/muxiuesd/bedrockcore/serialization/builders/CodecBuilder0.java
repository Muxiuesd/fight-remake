package game.muxiuesd.bedrockcore.serialization.builders;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.FieldBinding;
import game.muxiuesd.bedrockcore.serialization.ObjectCodec;
import game.muxiuesd.bedrockcore.serialization.ParamField;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 基础的编解码器构造器
 * */
public class CodecBuilder0<T> {

    public final List<ParamField<T, ?>> paramFields = new ArrayList<>();       //对象的构造方法的字段
    public final List<FieldBinding<T, ?>> setterFields = new ArrayList<>();    //普通字段


    /**
     * 声明第一个构造参数字段
     * */
    public <A> CodecBuilder1<T, A> paramField(String name, Function<T, ? extends A> getter, Codec<? extends A> codec) {
        paramFields.add(new ParamField<>(name, getter, codec));
        return new CodecBuilder1<>(this);
    }

    // 普通字段（setter 注入），返回自身以便链式调用
    public <F> CodecBuilder0<T> field (String name,
                                      Function<T, ? extends F> getter,
                                      BiConsumer<T, ? extends F> setter,
                                      Codec<? extends F> codec) {
        setterFields.add(new FieldBinding<>(name, getter, setter, codec, true));
        return this;
    }

    // 只编码不解码的字段
    public <F> CodecBuilder0<T> encoderField (String name, Function<T, F> getter, Codec<F> codec) {
        setterFields.add(new FieldBinding<>(name, getter, null, codec, false));
        return this;
    }

    // 无参工厂
    public Codec<T> noArgFactory (Supplier<T> factory) {
        if (!paramFields.isEmpty()) throw new IllegalStateException("Cannot use noArgFactory with paramFields");
        return build(args -> factory.get());
    }

    public Codec<T> build (Function<Object[], T> factory) {
        return new ObjectCodec<>(factory, paramFields, setterFields);
    }



}

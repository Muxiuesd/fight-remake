package game.muxiuesd.bedrockcore.serialization.builders;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.FieldBinding;
import game.muxiuesd.bedrockcore.serialization.ParamField;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

// ---------- 1 个构造参数 ----------
public class CodecBuilder1<T, A> {
    private final CodecBuilder0<T> base;

    CodecBuilder1(CodecBuilder0<T> base) { this.base = base; }

    /**
     *  声明第二个构造参数字段
     * */
    public <B> CodecBuilder2<T, A, B> paramField (String name,
                                                  Function<T, ? extends B> getter,
                                                  Codec<? extends B> codec) {
        base.paramFields.add(new ParamField<>(name, getter, codec));
        return new CodecBuilder2<>(base);
    }

    public <F> CodecBuilder1<T, A> field(String name,
                                         Function<T, ? extends F> getter,
                                         BiConsumer<T, ? extends F> setter,
                                         Codec<? extends F> codec) {
        base.setterFields.add(new FieldBinding<>(name, getter, setter, codec, true));
        return this;
    }

    public <F> CodecBuilder1<T, A> encoderField(String name, Function<T, F> getter, Codec<F> codec) {
        base.setterFields.add(new FieldBinding<>(name, getter, null, codec, false));
        return this;
    }

    /**
     * 可选字段：存档里缺这个字段不算错误
     * <p>
     * 缺字段时什么都不做，对象保留工厂/构造器构造出来的值
     * */
    public <F> CodecBuilder1<T, A> optionalField (String name,
                                    Function<T, ? extends F> getter,
                                    BiConsumer<T, ? extends F> setter,
                                    Codec<? extends F> codec) {
        base.setterFields.add(new FieldBinding<>(name, getter, setter, codec, true, true, null));
        return this;
    }

    /**
     * 可选字段：存档里缺这个字段时写入 defaultValue
     * <p>
     * 用 Supplier 而不是直接传值：默认值可能是可变对象（集合、背包），不能让所有对象共用一份
     * */
    public <F> CodecBuilder1<T, A> optionalField (String name,
                                    Function<T, ? extends F> getter,
                                    BiConsumer<T, ? extends F> setter,
                                    Codec<? extends F> codec,
                                    Supplier<? extends F> defaultValue) {
        base.setterFields.add(new FieldBinding<>(name, getter, setter, codec, true, true, defaultValue));
        return this;
    }

    public Codec<T> factory(Codec.Func1<A, T> factory) {
        return base.build(args -> factory.apply((A) args[0]));
    }
}

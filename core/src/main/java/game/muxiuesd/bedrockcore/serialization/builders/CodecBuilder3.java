package game.muxiuesd.bedrockcore.serialization.builders;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.FieldBinding;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

// ---------- 3 个构造参数 ----------
public class CodecBuilder3<T, A, B, C> {
    private final CodecBuilder0<T> base;

    CodecBuilder3(CodecBuilder0<T> base) { this.base = base; }

    /**
     * TODO 声明第四个构造参数字段
     * */

    /**
     *
     * */
    public <F> CodecBuilder3<T, A, B, C> field (String name,
                                                Function<T, ? extends F> getter,
                                                BiConsumer<T, ? extends F> setter,
                                                Codec<? extends F> codec) {
        base.setterFields.add(new FieldBinding<>(name, getter, setter, codec, true));
        return this;
    }

    public <F> CodecBuilder3<T, A, B, C> encoderField (String name, Function<T, F> getter, Codec<F> codec) {
        base.setterFields.add(new FieldBinding<>(name, getter, null, codec, false));
        return this;
    }

    /**
     * 可选字段：存档里缺这个字段不算错误
     * <p>
     * 缺字段时什么都不做，对象保留工厂/构造器构造出来的值
     * */
    public <F> CodecBuilder3<T, A, B, C> optionalField (String name,
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
    public <F> CodecBuilder3<T, A, B, C> optionalField (String name,
                                    Function<T, ? extends F> getter,
                                    BiConsumer<T, ? extends F> setter,
                                    Codec<? extends F> codec,
                                    Supplier<? extends F> defaultValue) {
        base.setterFields.add(new FieldBinding<>(name, getter, setter, codec, true, true, defaultValue));
        return this;
    }

    public Codec<T> factory (Codec.Func3<A, B, C, T> factory) {
        return base.build(args -> factory.apply((A) args[0], (B) args[1], (C) args[2]));
    }
}

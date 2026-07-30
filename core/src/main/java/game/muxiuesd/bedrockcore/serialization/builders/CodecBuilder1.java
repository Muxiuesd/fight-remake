package game.muxiuesd.bedrockcore.serialization.builders;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.FieldBinding;
import game.muxiuesd.bedrockcore.serialization.ParamField;

import java.util.function.BiConsumer;
import java.util.function.Function;

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

    public Codec<T> factory(Codec.Func1<A, T> factory) {
        return base.build(args -> factory.apply((A) args[0]));
    }
}

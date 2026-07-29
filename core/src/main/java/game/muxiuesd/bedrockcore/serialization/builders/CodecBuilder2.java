package game.muxiuesd.bedrockcore.serialization.builders;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.FieldBinding;
import game.muxiuesd.bedrockcore.serialization.ParamField;

import java.util.function.BiConsumer;
import java.util.function.Function;

// ---------- 2 个构造参数 ----------
public class CodecBuilder2<T, A, B> {
    private final CodecBuilder0<T> base;

    CodecBuilder2(CodecBuilder0<T> base) { this.base = base; }

    public <C> CodecBuilder3<T, A, B, C> paramField(String name, Function<T, C> getter, Codec<C> codec) {
        base.paramFields.add(new ParamField<>(name, getter, codec));
        return new CodecBuilder3<>(base);
    }

    public <F> CodecBuilder2<T, A, B> field(String name, Function<T, F> getter, BiConsumer<T, F> setter, Codec<F> codec) {
        base.setterFields.add(new FieldBinding<>(name, getter, setter, codec, true));
        return this;
    }

    public <F> CodecBuilder2<T, A, B> encoderField(String name, Function<T, F> getter, Codec<F> codec) {
        base.setterFields.add(new FieldBinding<>(name, getter, null, codec, false));
        return this;
    }

    public Codec<T> factory(Codec.Func2<A, B, T> factory) {
        return base.build(args -> factory.apply((A) args[0], (B) args[1]));
    }
}

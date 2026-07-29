package game.muxiuesd.bedrockcore.serialization.builders;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.FieldBinding;

import java.util.function.BiConsumer;
import java.util.function.Function;

// ---------- 3 个构造参数 ----------
public class CodecBuilder3<T, A, B, C> {
    private final CodecBuilder0<T> base;

    CodecBuilder3(CodecBuilder0<T> base) { this.base = base; }



    public <F> CodecBuilder3<T, A, B, C> field (String name, Function<T, F> getter, BiConsumer<T, F> setter, Codec<F> codec) {
        base.setterFields.add(new FieldBinding<>(name, getter, setter, codec, true));
        return this;
    }

    public <F> CodecBuilder3<T, A, B, C> encoderField (String name, Function<T, F> getter, Codec<F> codec) {
        base.setterFields.add(new FieldBinding<>(name, getter, null, codec, false));
        return this;
    }

    public Codec<T> factory (Codec.Func3<A, B, C, T> factory) {
        return base.build(args -> factory.apply((A) args[0], (B) args[1], (C) args[2]));
    }
}

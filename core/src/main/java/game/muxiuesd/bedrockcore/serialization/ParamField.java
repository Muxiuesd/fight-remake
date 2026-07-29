package game.muxiuesd.bedrockcore.serialization;

import java.util.function.Function;

/**
 * 内部数据类
 * */
public class ParamField<T, F> {
    public final String name;
    public final Function<T, F> getter;
    public final Codec<F> codec;

    public ParamField(String name, Function<T, F> getter, Codec<F> codec) {
        this.name = name; this.getter = getter; this.codec = codec;
    }
}

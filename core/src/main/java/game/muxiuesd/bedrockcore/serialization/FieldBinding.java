package game.muxiuesd.bedrockcore.serialization;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 字段绑定
 * */
public class FieldBinding<T, F> {
    public final String name;              //字段名称
    public final Function<T, ? extends F> getter;    //字段的获取方法接口
    public final BiConsumer<T, ? extends F> setter;  //字段的设置方法接口
    public final Codec<? extends F> codec;          //字段的编解码器
    public final boolean decodeOnRead;

    public FieldBinding(String name,
                        Function<T, ? extends F> getter, BiConsumer<T, ? extends F> setter,
                        Codec<? extends F> codec, boolean decodeOnRead) {
        this.name = name; this.getter = getter; this.setter = setter;
        this.codec = codec; this.decodeOnRead = decodeOnRead;
    }
}

package game.muxiuesd.bedrockcore.serialization;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 字段绑定
 * */
public class FieldBinding<T, F> {
    public final String name;              //字段名称
    public final Function<T, ? extends F> getter;    //字段的获取方法接口
    public final BiConsumer<T, ? extends F> setter;  //字段的设置方法接口
    public final Codec<? extends F> codec;          //字段的编解码器
    public final boolean decodeOnRead;
    //字段缺失时是否算错误：true = 可缺（用默认值，不计入解码错误）；false = 必填
    public final boolean optional;
    //可选字段缺失时写入的值；null 表示保持工厂/构造器构造出来的值不动
    public final Supplier<? extends F> defaultValue;

    public FieldBinding(String name,
                        Function<T, ? extends F> getter, BiConsumer<T, ? extends F> setter,
                        Codec<? extends F> codec, boolean decodeOnRead) {
        this(name, getter, setter, codec, decodeOnRead, false, null);
    }

    public FieldBinding(String name,
                        Function<T, ? extends F> getter, BiConsumer<T, ? extends F> setter,
                        Codec<? extends F> codec, boolean decodeOnRead,
                        boolean optional, Supplier<? extends F> defaultValue) {
        this.name = name; this.getter = getter; this.setter = setter;
        this.codec = codec; this.decodeOnRead = decodeOnRead;
        this.optional = optional; this.defaultValue = defaultValue;
    }
}

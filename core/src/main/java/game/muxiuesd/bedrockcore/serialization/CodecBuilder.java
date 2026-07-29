package game.muxiuesd.bedrockcore.serialization;

import game.muxiuesd.bedrockcore.serialization.builders.CodecBuilder0;

/**
 * 编解码器的构造器
 * */
public class CodecBuilder<T> {
    /**
     * 创建一个编解码器，对象的工厂需要后面指定
     */
    public static <T> CodecBuilder0<T> create () {
        //最先返回一个无构造参数的编解码器
        return new CodecBuilder0<>();
    }
}



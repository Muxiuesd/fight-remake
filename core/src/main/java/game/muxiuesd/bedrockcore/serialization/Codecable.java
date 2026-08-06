package game.muxiuesd.bedrockcore.serialization;

/**
 * 可以编解码的类继承的接口
 * */
public interface Codecable<T> {
    Codec<T> getCodec();
}

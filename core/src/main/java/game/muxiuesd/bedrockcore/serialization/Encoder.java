package game.muxiuesd.bedrockcore.serialization;

/**
 * 编码器
 * */
public interface Encoder<T> {
    /**
     * 编码成指定格式
     * */
    void encode (T obj);
}

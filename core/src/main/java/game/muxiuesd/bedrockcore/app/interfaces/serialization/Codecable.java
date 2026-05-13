package game.muxiuesd.bedrockcore.app.interfaces.serialization;

import game.muxiuesd.bedrockcore.app.interfaces.data.DataReader;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataWriter;

/**
 * 接口：可编解码
 * */
public interface Codecable<T, W extends DataWriter<W>, R extends DataReader<R>> {
    /**
     * 获取编解码器
     * @return 返回注册过的编解码器
     * */
    Codec<T, W, R> getCodec ();
}

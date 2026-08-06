package ttk.muxiuesd.serialization.abs;

import game.muxiuesd.bedrockcore.app.interfaces.data.DataReader;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataWriter;

import java.util.HashMap;

/**
 * 可编码的HashMap
 * <p>
 * @deprecated 已弃用：基于旧式数据接口（{@link DataWriter}/{@link DataReader}）的抽象类，
 * 已被新式编解码器（RawObject 格式）替代，当前无任何使用者
 * */
@Deprecated
public abstract class HashMapCodec <T, K, V, W extends DataWriter<?>, R extends DataReader<?>> extends HashMap<K, V> {
    /**
     * 编码
     * */
    public abstract void encode (W writer);
    /**
     * 解码
     * */
    public abstract void decode (R reader);
}

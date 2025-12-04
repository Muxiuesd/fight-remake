package ttk.muxiuesd.interfaces.serialization;

import ttk.muxiuesd.interfaces.data.DataReader;
import ttk.muxiuesd.interfaces.data.DataWriter;

/**
 * 接口：可编解码
 * */
public interface Codecable<T, W extends DataWriter<?>, R extends DataReader<?>> {
    /**
     * 获取编解码器
     * @return 返回注册过的编解码器
     * */
    Codec<T, W, R> getCodec();
}

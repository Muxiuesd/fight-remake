package ttk.muxiuesd.interfaces.serialization;

import ttk.muxiuesd.interfaces.data.DataReader;
import ttk.muxiuesd.interfaces.data.DataWriter;

import java.util.Optional;

/**
 * 编解码，将一个类编码成指定格式或者从指定格式中解码成类
 *
 * @param <T> 需要被编解码的类型
 * @param <W> 数据写入类
 * @param <R> 数据读取类
 * */
public interface Codec<T, W extends DataWriter<?>, R extends DataReader<?>> {
    /**
     * 编码成指定格式
     * */
    void encode (T obj, W dataWriter);

    /**
     * 解码成类
     * */
    Optional<T> decode (R dataReader);
}

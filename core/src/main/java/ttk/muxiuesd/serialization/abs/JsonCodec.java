package ttk.muxiuesd.serialization.abs;

import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.interfaces.data.DataReader;
import ttk.muxiuesd.interfaces.serialization.Codec;

import java.util.Optional;

/**
 * json类型的编解码器
 * */
public abstract class JsonCodec<T> implements Codec<T, JsonDataWriter, JsonDataReader> {

    @Override
    public Optional<T> decode (DataReader<JsonDataReader> dataReader) {
        return this.parse(dataReader);
    }
    /**
     * 解析出结果
     * */
    protected abstract Optional<T> parse (DataReader<JsonDataReader> dataReader);
}

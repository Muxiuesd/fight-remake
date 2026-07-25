package ttk.muxiuesd.serialization.abs;

import game.muxiuesd.bedrockcore.data.JsonDataReader;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import game.muxiuesd.bedrockcore.app.interfaces.serialization.Codec;

import java.util.Optional;

/**
 * json类型的编解码器
 * */
public abstract class JsonCodec<T> implements Codec<T, JsonDataWriter, JsonDataReader> {

    @Override
    public Optional<T> decode (JsonDataReader dataReader) {
        return this.parse(dataReader);
    }

    /**
     * 解析出结果
     * */
    public abstract Optional<T> parse (JsonDataReader dataReader);
}

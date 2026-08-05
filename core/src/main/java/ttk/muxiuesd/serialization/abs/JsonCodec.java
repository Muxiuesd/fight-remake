package ttk.muxiuesd.serialization.abs;

import game.muxiuesd.bedrockcore.data.JsonDataReader;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import game.muxiuesd.bedrockcore.app.interfaces.serialization.Codec;

import java.util.Optional;

/**
 * json类型的编解码器
 * <p>
 * @deprecated 已弃用：基于旧式编解码器（JSON 树格式）的抽象类，
 * 已被新式编解码器（{@link game.muxiuesd.bedrockcore.serialization.Codec}，RawObject 格式）替代
 * */
@Deprecated
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

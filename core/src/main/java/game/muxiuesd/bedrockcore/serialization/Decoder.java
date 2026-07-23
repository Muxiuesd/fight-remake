package game.muxiuesd.bedrockcore.serialization;

import java.util.Optional;

/**
 * 解码器
 * */
public interface Decoder<T> {
    /**
     * 解码成类
     * */
    Optional<T> decode ();
}

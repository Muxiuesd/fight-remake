package ttk.muxiuesd.serialization;

import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.interfaces.data.DataReader;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.chunk.Chunk;

import java.util.Optional;

/**
 * 对于区块的编解码
 * */
public class ChunkCodec extends JsonCodec<Chunk> {
    @Override
    public void encode (Chunk chunk, JsonDataWriter dataWriter) {
        chunk.traversal((x, y) -> {
            Block block = chunk.getBlock(x, y);
            dataWriter.objStart(x + "," + y);
            Codecs.BLOCK.encode(block, dataWriter);
            dataWriter.objEnd();
        });
    }

    @Override
    protected Optional<Chunk> parse (DataReader<JsonDataReader> dataReader) {
        return Optional.empty();
    }
}

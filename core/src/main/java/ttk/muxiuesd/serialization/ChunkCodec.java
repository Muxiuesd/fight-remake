package ttk.muxiuesd.serialization;

import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.chunk.Chunk;
import ttk.muxiuesd.world.wall.Wall;

import java.util.Optional;

/**
 * 对于区块的编解码
 * */
public class ChunkCodec extends JsonCodec<Chunk> {
    @Override
    public void encode (Chunk chunk, JsonDataWriter dataWriter) {
        //方块
        dataWriter.objStart("blocks");
        chunk.traversal((x, y) -> {
            Block block = chunk.getBlock(x, y);
            dataWriter.objStart(x + "," + y);
            Codecs.BLOCK.encode(block, dataWriter);
            dataWriter.objEnd();
        });
        dataWriter.objEnd();

        //墙体
        dataWriter.objStart("walls");
        chunk.traversal((x, y) -> {
            Wall<?> wall = chunk.getWall(x, y);
            if (wall != null) {
                dataWriter.objStart(x + "," + y);
                Codecs.WALL.encode(wall, dataWriter);
                dataWriter.objEnd();
            }
        });
        dataWriter.objEnd();

        //高度信息
        dataWriter.objStart("heights");
        chunk.traversal((x, y) -> {
            int height = chunk.getHeight(x, y);
            dataWriter.writeInt(x + "," + y, height);
        });
        dataWriter.objEnd();
    }

    @Override
    protected Optional<Chunk> parse (JsonDataReader dataReader) {
        Chunk chunk = new Chunk();
        return Optional.of(chunk);
    }
}

package ttk.muxiuesd.serialization;

import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.chunk.Chunk;
import ttk.muxiuesd.world.wall.Wall;

/**
 * 所有编解码器
 * */
public class Codecs {
    public static final JsonCodec<Block> BLOCK = new BlockCodec();
    public static final JsonCodec<Wall<?>> WALL = new WallCodec();
    public static final JsonCodec<Chunk> CHUNK = new ChunkCodec();
}

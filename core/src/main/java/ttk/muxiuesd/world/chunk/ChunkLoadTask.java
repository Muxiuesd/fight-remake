package ttk.muxiuesd.world.chunk;

import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.world.chunk.abs.ChunkTask;

/**异步加载Chunk的任务
 * */
public class ChunkLoadTask extends ChunkTask {
    public ChunkLoadTask(ChunkSystem chunkSystem, ChunkPosition chunkPosition) {
        super(chunkSystem, chunkPosition);
    }

    @Override
    public Chunk call() throws Exception {
        Chunk chunk = new Chunk(getChunkSystem());
        chunk.setChunkPosition(getChunkPosition());
        chunk.initBlock();
        chunk.initWall();
        return chunk;
    }
}


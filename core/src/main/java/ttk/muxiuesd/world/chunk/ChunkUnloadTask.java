package ttk.muxiuesd.world.chunk;

import ttk.muxiuesd.system.ChunkSystem;

/**
 * 多线程卸载区块任务
 */
public class ChunkUnloadTask extends ChunkTask {
    private Chunk chunk;

    public ChunkUnloadTask(ChunkSystem chunkSystem, Chunk chunk) {
        super(chunkSystem, chunk.getChunkPosition());
        this.chunk = chunk;
    }

    @Override
    public Chunk call() throws Exception {
        this.chunk.dispose();
        return this.chunk;
    }
}

package ttk.muxiuesd.world.chunk.abs;

import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.world.chunk.Chunk;

import java.util.concurrent.Callable;

public abstract class ChunkTask implements Callable<Chunk> {
    private final ChunkSystem chunkSystem;
    private final ChunkPosition chunkPosition;

    public ChunkTask(ChunkSystem chunkSystem, ChunkPosition chunkPosition) {
        this.chunkSystem = chunkSystem;
        this.chunkPosition = chunkPosition;
    }

    @Override
    public abstract Chunk call() throws Exception;

    public ChunkSystem getChunkSystem() {
        return chunkSystem;
    }

    public ChunkPosition getChunkPosition() {
        return chunkPosition;
    }
}

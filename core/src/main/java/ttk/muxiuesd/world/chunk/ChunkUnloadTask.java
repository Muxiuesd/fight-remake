package ttk.muxiuesd.world.chunk;

import ttk.muxiuesd.data.ChunkJsonDataOutput;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.serialization.Codecs;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.world.chunk.abs.ChunkTask;

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
    public Chunk call() {
        JsonDataWriter chunkDataWriter = new JsonDataWriter();
        chunkDataWriter.objStart();
        Codecs.CHUNK.encode(this.chunk, chunkDataWriter);
        chunkDataWriter.objEnd();
        new ChunkJsonDataOutput(chunk.getChunkPosition().toString()).output(chunkDataWriter);

        return this.chunk;
    }
}

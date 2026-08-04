package ttk.muxiuesd.world.chunk;

import game.muxiuesd.bedrockcore.serialization.RawObject;
import game.muxiuesd.bedrockcore.serialization.RawObjectJsonConverter;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.AbsFileUtil;
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
        RawObject rawObject = Chunk.CODEC.encode(this.chunk);
        String json = RawObjectJsonConverter.toJson(rawObject);
        AbsFileUtil.createFile(Fight.getPathSaveChunks(), this.chunk.getChunkPosition().toString() + ".json")
            .writeString(json, false);

        return this.chunk;
    }
}

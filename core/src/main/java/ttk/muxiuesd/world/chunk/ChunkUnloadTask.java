package ttk.muxiuesd.world.chunk;

import game.muxiuesd.bedrockcore.serialization.RawObject;
import game.muxiuesd.bedrockcore.serialization.RawObjectJsonConverter;
import game.muxiuesd.bedrockcore.util.UnifiedFileUtil;
import ttk.muxiuesd.Fight;
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
        RawObject rawObject = Chunk.CODEC.encode(this.chunk);
        String json = RawObjectJsonConverter.toJson(rawObject);
        //原子写入：先写临时文件再重命名，防止写盘被中断导致半截文件损坏存档
        UnifiedFileUtil.writeFileAtomic(
            Fight.getPathSaveChunks(),
            this.chunk.getChunkPosition().toString() + ".json",
            json);

        return this.chunk;
    }
}

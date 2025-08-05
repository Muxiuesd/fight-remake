package ttk.muxiuesd.world.chunk;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.registry.Codecs;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.util.FileUtil;
import ttk.muxiuesd.world.chunk.abs.ChunkGenerator;
import ttk.muxiuesd.world.chunk.abs.ChunkTask;

import java.util.Optional;

/**
 * 异步加载Chunk的任务
 * */
public class ChunkLoadTask extends ChunkTask {
    public ChunkLoadTask(ChunkSystem chunkSystem, ChunkPosition chunkPosition) {
        super(chunkSystem, chunkPosition);
    }

    @Override
    public Chunk call() {
        //TODO 加载保存过的区块
        String name = getChunkPosition().toString() + ".json";
        if (! FileUtil.fileExists(Fight.PATH_SAVE_CHUNKS, name)) {
            //文件不存在，新生成
            Chunk chunk = this.genNewChunk();
            chunk.setChunkPosition(getChunkPosition());
            chunk.setChunkSystem(getChunkSystem());
            return chunk;
        }
        //文件存在，就从文件加载区块
        Optional<Chunk> optional;
        //TODO 修复读取内容为空
        String file = FileUtil.readFileAsString(Fight.PATH_SAVE_CHUNKS, name);
        JsonDataReader dataReader;
        try {
            dataReader = new JsonDataReader(file);
            optional = Codecs.CHUNK.decode(
                dataReader
            );
        } catch (Exception e) {
            //尝试从正在卸载的区块里获取
            return getChunkSystem().getUnloadedChunk(getChunkPosition());
        }
        Chunk chunk = optional.orElse(this.genNewChunk());
        chunk.setChunkPosition(getChunkPosition());
        chunk.setChunkSystem(getChunkSystem());

        return chunk;

    }

    /**
     * 新生成一个区块
     * */
    private Chunk genNewChunk() {
        ChunkGenerator generator = getChunkSystem().getChunkGenerator();
        return generator.generate(getChunkPosition());
    }
}


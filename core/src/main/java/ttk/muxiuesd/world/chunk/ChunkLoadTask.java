package ttk.muxiuesd.world.chunk;

import game.muxiuesd.bedrockcore.serialization.DataResult;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import game.muxiuesd.bedrockcore.serialization.RawObjectJsonConverter;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.AbsFileUtil;
import ttk.muxiuesd.util.ChunkPosition;
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
        String name = getChunkPosition().toString() + ".json";
        if (! AbsFileUtil.fileExists(Fight.getPathSaveChunks(), name)) {
            //文件不存在，新生成
            Chunk chunk = this.genNewChunk();
            chunk.setChunkPosition(getChunkPosition());
            chunk.setChunkSystem(getChunkSystem());
            return chunk;
        }
        //文件存在，就从文件加载区块
        Optional<Chunk> optional = Optional.empty();
        String file = AbsFileUtil.readFileAsString(Fight.getPathSaveChunks(), name);
        try {
            RawObject rawObject = RawObjectJsonConverter.fromJson(file);
            DataResult<Chunk> decode = Chunk.CODEC.decode(rawObject);
            //部分字段解码失败时也会保留已经解码的数据
            if (decode.result().isPresent()) {
                optional = Optional.of(decode.result().get());
            }
            if (decode.error().isPresent()) {
                System.out.println("区块文件解码错误：" + name + "，错误：" + decode.error().get());
            }
        } catch (Exception e) {
            //e.printStackTrace();
            //尝试从正在卸载的区块里获取
            Chunk unloadedChunk = getChunkSystem().getUnloadedChunk(getChunkPosition());
            if (unloadedChunk != null) return unloadedChunk;
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

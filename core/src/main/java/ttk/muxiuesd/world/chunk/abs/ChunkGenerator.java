package ttk.muxiuesd.world.chunk.abs;

import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.util.SimplexNoise2D;
import ttk.muxiuesd.util.WorldMapNoise;
import ttk.muxiuesd.world.chunk.Chunk;


/**
 * 区块生成器
 * <p>
 * 不同的世界可以有不同的区块生成器，甚至有多个区块生成器
 * */
public abstract class ChunkGenerator {
    private ChunkSystem chunkSystem;

    public ChunkGenerator(ChunkSystem chunkSystem) {
        this.chunkSystem = chunkSystem;
    }

    /**
     * 生成逻辑
     * */
    public abstract Chunk generate(ChunkPosition chunkPosition);

    /**
     * 方块选择逻辑
     * */
    public abstract String chooseBlock(int height);

    /**
     * 生成地形
     * @param wx    方块在世界的横坐标
     * @param wy    方块在世界的纵坐标
     * @return 返回地形高度
     */
    protected int generateTerrain(float wx, float wy) {
        WorldMapNoise noise = this.chunkSystem.getNoise();
        double v = noise.noise(wx/ ChunkSystem.Slope, wy/ ChunkSystem.Slope);
        return (int) SimplexNoise2D.map(v, -1f, 1f, Chunk.LowestHeight, Chunk.HighestHeight);
    }


    public ChunkSystem getChunkSystem () {
        return this.chunkSystem;
    }

    public void setChunkSystem (ChunkSystem chunkSystem) {
        this.chunkSystem = chunkSystem;
    }
}

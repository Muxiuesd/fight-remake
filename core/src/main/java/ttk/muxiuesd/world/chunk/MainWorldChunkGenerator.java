package ttk.muxiuesd.world.chunk;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.registry.Walls;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.util.WorldMapNoise;
import ttk.muxiuesd.world.biome.Biome;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.instance.BlockWater;
import ttk.muxiuesd.world.chunk.abs.ChunkGenerator;
import ttk.muxiuesd.world.wall.Wall;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 主世界区块生成器
 * <p>
 * 区块生成流程：Voronoi 判海陆 → 海为 OCEAN；陆则按"区块总体高度 + 温度 + 湿度"查表得陆地群系。
 * 区块内每个格子按其高度在群系内决定方块，河流/湖泊压成水域。
 */
public class MainWorldChunkGenerator extends ChunkGenerator {
    public MainWorldChunkGenerator (ChunkSystem chunkSystem) {
        super(chunkSystem);
    }

    @Override
    public Chunk generate (ChunkPosition chunkPosition) {
        ChunkSystem cs = getChunkSystem();
        //区块中心世界坐标（用中心高度代表区块总体高度）
        float centerX = chunkPosition.x * Chunk.ChunkWidth + Chunk.ChunkWidth / 2f;
        float centerY = chunkPosition.y * Chunk.ChunkHeight + Chunk.ChunkHeight / 2f;

        //① 判定区块群系：海陆 → 陆地查表
        Biome biome;
        if (cs.isOcean(centerX, centerY)) {
            biome = cs.getOceanBiome();
        } else {
            int chunkHeight = landHeight(centerX, centerY);   //区块总体高度（陆地高度，0~128）
            double temp  = cs.sampleTemp(centerX, centerY);
            double humid = cs.sampleHumidity(centerX, centerY);
            biome = cs.lookupLandBiome(temp, humid, chunkHeight);
        }

        Chunk chunk = new Chunk(cs);
        chunk.setChunkPosition(chunkPosition);
        chunk.setBiome(biome);
        chunk.setCanSpawn(biome.getCanSpawn());   //出生能力来自群系注册阶段确定

        //② 遍历区块内每个格子：按高度 + 群系决定方块
        chunk.traversal((x, y) -> {
            float wx = chunk.getWorldX(x);
            float wy = chunk.getWorldY(y);
            //区分海陆的地形高度：
            //海洋区块高度 map 到 [-128, 0]（全水下）；陆地区块高度 map 到 [0, 128]（非负，陆地不积水）
            int height = biome.isOcean()
                ? this.oceanHeight(wx, wy)
                : this.landHeight(wx, wy);
            //河流/湖泊（陆地内）压成水下
            if (!biome.isOcean()) {
                if (cs.isRiverCell(wx, wy)) height = Chunk.SEA_LEVEL - 5;
                else if (cs.isLakeCell(wx, wy)) height = Chunk.SEA_LEVEL - 6;
            }

            Block block = biome.decideBlock(height);
            chunk.setBlock(block, x, y);
            chunk.setHeight(x, y, height);
        });

        //③ 墙体（水上不生成，其余地格低概率）
        chunk.traversal((x, y) -> {
            if (chunk.getBlock(x, y) instanceof BlockWater) return;
            //使用 ThreadLocalRandom：区块生成在线程池并发执行，MathUtils.random 内部共享 Random 非线程安全
            int random = ThreadLocalRandom.current().nextInt(0, 16);
            if (random < 1) {
                Wall<?> wall = Walls.SMOOTH_STONE.createSelf(new Vector2(chunk.getWorldX(x), chunk.getWorldY(y)));
                chunk.setWall(wall, x, y);
            }
        });

        return chunk;
    }

    @Override
    public String chooseBlock (int height) {
        //已废弃：方块选择改由 Biome.decideBlock 按群系+高度决定
        return null;
    }

    /**
     * 海洋地形高度：map 到 [LowestHeight, SEA_LEVEL]（即 [-128, 0]），全水下
     */
    private int oceanHeight (float wx, float wy) {
        double v = this.baseNoise(wx, wy);
        return (int) WorldMapNoise.map(v, -1f, 1f, Chunk.LowestHeight, Chunk.SEA_LEVEL - 1);
    }

    /**
     * 陆地地形高度：map 到 [SEA_LEVEL, HighestHeight]（即 [0, 128]），陆地不积水
     */
    private int landHeight (float wx, float wy) {
        double v = this.baseNoise(wx, wy);
        return (int) WorldMapNoise.map(v, -1f, 1f, Chunk.SEA_LEVEL + 1, Chunk.HighestHeight);
    }

    /**
     * 地形基础噪声值（复用 generateTerrain 的采样频率）
     */
    private double baseNoise (float wx, float wy) {
        WorldMapNoise noise = getChunkSystem().getWorldNoise();
        return noise.noise(wx / ChunkSystem.Slope, wy / ChunkSystem.Slope);
    }
}
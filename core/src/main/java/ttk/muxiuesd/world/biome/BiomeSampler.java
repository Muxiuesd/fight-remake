package ttk.muxiuesd.world.biome;

import ttk.muxiuesd.registry.Biomes;
import ttk.muxiuesd.util.WorldMapNoise;
import ttk.muxiuesd.world.chunk.Chunk;

import java.util.Collection;

/**
 * 群系采样器
 * <p>
 * 负责：① Voronoi 近邻划分判定海陆（概率权重控海陆比例）；② 河流/湖泊格子级判定；
 * ③ 温度/湿度采样；④ 陆地群系查表（温度+湿度+区块总体高度）。
 * 全部基于世界种子，确定性可复现。
 */
public class BiomeSampler {
    private final long seed;
    private final WorldMapNoise noise;   //复用世界种子的噪声

    // Voronoi 群系块密度（平均约 64 区块）
    private final float clusterSize = 128f;
    // 海陆权重：海洋占比（0.7 = 海 70%、陆 30%）
    private static final double OCEAN_WEIGHT = 0.7;

    // 温度/湿度/河流/湖泊 的采样频率与偏移（不同频率×偏移 → 多个互不相关的连续场）
    private static final float TEMP_FREQ = 0.004f, TEMP_OFFSET = 0f;
    private static final float HUMID_FREQ = 0.004f, HUMID_OFFSET = 100f;
    private static final float RIVER_FREQ = 0.002f, RIVER_OFFSET = 200f, RIVER_BAND = 0.85f;
    private static final float LAKE_FREQ = 0.01f, LAKE_OFFSET = 300f, LAKE_THRESHOLD = 0.75f;

    public BiomeSampler (long seed, Collection<Biome> biomes) {
        this.seed = seed;
        this.noise = new WorldMapNoise(seed);
    }

    /**
     * 海陆判定：Voronoi 最近种子点是否海洋
     */
    public boolean isOcean (float wx, float wy) {
        return this.nearestSeed(wx, wy).isOcean;
    }

    /**
     * 河流判定：噪声等高线带（蛇形带状水域）
     */
    public boolean isRiverCell (float wx, float wy) {
        double v = this.noise.noise(wx * RIVER_FREQ + RIVER_OFFSET, wy * RIVER_FREQ + RIVER_OFFSET);
        return Math.abs(v) > RIVER_BAND;
    }

    /**
     * 湖泊判定：噪声斑块（闭合水域）
     */
    public boolean isLakeCell (float wx, float wy) {
        double v = this.noise.noise(wx * LAKE_FREQ + LAKE_OFFSET, wy * LAKE_FREQ + LAKE_OFFSET);
        return v > LAKE_THRESHOLD;
    }

    /**
     * 温度采样 [0,1]
     */
    public double sampleTemp (float wx, float wy) {
        return this.noise.getNorNoise(wx * TEMP_FREQ + TEMP_OFFSET, wy * TEMP_FREQ + TEMP_OFFSET, TEMP_FREQ);
    }

    /**
     * 湿度采样 [0,1]
     */
    public double sampleHumidity (float wx, float wy) {
        return this.noise.getNorNoise(wx * HUMID_FREQ + HUMID_OFFSET, wy * HUMID_FREQ + HUMID_OFFSET, HUMID_FREQ);
    }

    /**
     * 陆地群系查表：温度 + 湿度 + 区块总体高度
     * <p>
     * ① 低海拔（≤CHUNK_LOW_TOP）→ 湿地；高海拔（≥CHUNK_HIGH_BOTTOM）→ 山地；
     * ② 中海拔 → 按温度湿度细分（雪原/沙漠/森林/平原）。
     */
    public Biome lookupLandBiome (double temp, double humid, int chunkHeight) {
        if (chunkHeight <= Chunk.CHUNK_LOW_TOP) return Biomes.WETLAND;
        if (chunkHeight >= Chunk.CHUNK_HIGH_BOTTOM) return Biomes.MOUNTAIN;

        if (temp < 0.35) return Biomes.SNOWY;
        if (temp > 0.65 && humid < 0.4) return Biomes.DESERT;
        if (humid > 0.6) return Biomes.FOREST;
        return Biomes.PLAINS;
    }

    /**
     * Voronoi 种子点：位置在 cell 内随机偏移，isOcean 由概率权重决定
     */
    private VoronoiSeed nearestSeed (float wx, float wy) {
        int cx = (int) Math.floor(wx / this.clusterSize);
        int cy = (int) Math.floor(wy / this.clusterSize);
        VoronoiSeed best = null;
        float bestDist = Float.MAX_VALUE;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                VoronoiSeed s = this.seedAt(cx + dx, cy + dy);
                float d = (s.x - wx) * (s.x - wx) + (s.y - wy) * (s.y - wy);
                if (d < bestDist) {
                    bestDist = d;
                    best = s;
                }
            }
        }
        return best;
    }

    private VoronoiSeed seedAt (int cx, int cy) {
        float x = (float) ((cx + hash01(cx, cy, this.seed)) * this.clusterSize);
        float y = (float) ((cy + hash01(cx, cy, this.seed + 1)) * this.clusterSize);
        // 概率权重：hash(0~1) < 海洋权重(0.7) → 海洋种子点
        boolean isOcean = hash01(cx, cy, this.seed + 2) < OCEAN_WEIGHT;
        return new VoronoiSeed(x, y, isOcean);
    }

    /**
     * 由坐标+种子生成确定性 [0,1) 哈希（均匀分布）
     * <p>
     * 用 splitmix64 高质量散列：对 x、y、seed 的变化产生均匀的伪随机数。
     * 不能用 longBitsToDouble（把 long 位模式解释为 double 会产生 NaN/极大值，破坏 Voronoi 判定）。
     */
    private double hash01 (int x, int y, long seed) {
        long h = seed ^ Long.rotateLeft(x * 0x9E3779B97F4A7C15L, 32)
                      ^ Long.rotateLeft(y * 0xBF58476D1CE4E5B9L, 32);
        // splitmix64 混淆
        h ^= h >>> 33;
        h *= 0xFF51AFD7ED558CCDL;
        h ^= h >>> 33;
        h *= 0xC4CEB9FE1A85EC53L;
        h ^= h >>> 33;
        // 取高 53 位映射到 [0,1)
        return (h >>> 11) * (1.0 / 9007199254740992.0);
    }

    /**
     * Voronoi 种子点：位置 + 是否海洋
     */
    private static class VoronoiSeed {
        final float x, y;
        final boolean isOcean;

        VoronoiSeed (float x, float y, boolean isOcean) {
            this.x = x;
            this.y = y;
            this.isOcean = isOcean;
        }
    }
}
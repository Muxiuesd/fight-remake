package ttk.muxiuesd.world.biome;

import ttk.muxiuesd.registry.Biomes;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.WorldMapNoise;
import ttk.muxiuesd.world.chunk.Chunk;

import java.util.Collection;

/**
 * 群系采样器
 * <p>
 * 负责：① 地形高度采样（海陆由高度判定，非 Voronoi）；② 区块级 3×3 平滑高度判定海陆归属；
 * ③ 温度/湿度采样；④ 陆地群系查表（高度分段 + 温度 + 湿度）。
 * 全部基于世界种子，确定性可复现。
 */
public class BiomeSampler {
    private final long seed;
    private final WorldMapNoise noise;   //复用世界种子的噪声

// 温度/湿度/河流/湖泊 的采样频率与偏移
private static final float TEMP_LOW_FREQ = 0.0006f, TEMP_HIGH_FREQ = 0.002f, TEMP_OFFSET = 0f;
    //湿度用与温度不同的相位大偏移，使其与温度场相对独立（否则温湿强正相关致无沙漠气候）；频率仍低频保持湿地带连续
    private static final float HUMID_LOW_FREQ = 0.0006f, HUMID_HIGH_FREQ = 0.002f, HUMID_OFFSET = 3000f;
    private static final float RIVER_FREQ = 0.002f, RIVER_OFFSET = 200f, RIVER_BAND = 0.85f;
    private static final float LAKE_FREQ = 0.01f, LAKE_OFFSET = 300f, LAKE_THRESHOLD = 0.75f;

    public BiomeSampler (long seed, Collection<Biome> biomes) {
        this.seed = seed;
        this.noise = new WorldMapNoise(seed);
    }

    /**
     * 逐格地形高度 [0, 256]（纯噪声标量，与海陆解耦，可先于群系独立算）
     */
    public int sampleHeight (float wx, float wy) {
        double v = this.noise.noise(wx / ChunkSystem.Slope, wy / ChunkSystem.Slope);
        return (int) Math.round(WorldMapNoise.map(v, -1, 1, Chunk.LowestHeight, Chunk.HighestHeight));
    }

    /**
     * 某世界坐标是否海洋：取该坐标所在区块，用 3×3 邻域区块的<b>中心高度</b>平滑后与海平面阈值比较。
     * <p>
     * 3×3 平滑让海陆分界（SEA_LEVEL 等高线）平滑连续，避免单区块平均导致的锯齿破碎。
     */
    public boolean isOcean (float wx, float wy) {
        int chunkX = chunkIndex(wx);
        int chunkY = chunkIndex(wy);
        return smoothedChunkHeight(chunkX, chunkY) < Chunk.SEA_LEVEL;
    }

    /**
     * 区块中心高度（该区块中心格的地形高度）
     */
    public int chunkCenterHeight (int chunkX, int chunkY) {
        float cx = chunkX * Chunk.ChunkWidth + Chunk.ChunkWidth / 2f;
        float cy = chunkY * Chunk.ChunkHeight + Chunk.ChunkHeight / 2f;
        return this.sampleHeight(cx, cy);
    }

    /**
     * 区块级 3×3 平滑高度：该区块与其 8 个邻区块的中心高度平均。
     * 用于判定区块海陆归属（决定 biome 标签 / canSpawn）。
     */
    public int smoothedChunkHeight (int chunkX, int chunkY) {
        long sum = 0;
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                sum += this.chunkCenterHeight(chunkX + dx, chunkY + dy);
                count++;
            }
        }
        return (int) Math.round(sum / (double) count);
    }

    private static int chunkIndex (float worldCoord) {
        return (int) Math.floor(worldCoord / Chunk.ChunkWidth);
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
     * 湿地水塘判定：比普通湖泊更易触发（阈值更低、斑块更密），让湿地呈现"低地草地 + 散布浅水塘"。
     */
    public boolean isWetlandPondCell (float wx, float wy) {
        double v = this.noise.noise(wx * 0.02f + 400f, wy * 0.02f + 400f);
        return v > 0.35;
    }

    /**
     * 温度采样 [0,1]（FBM 多层：低频定大块生态，高频扰动使边界自然弯曲、格子级渐变）
     */
    public double sampleTemp (float wx, float wy) {
        double low  = this.noise.getNorNoise(wx + TEMP_OFFSET, wy + TEMP_OFFSET, TEMP_LOW_FREQ);
        double high = this.noise.getNorNoise(wx + TEMP_OFFSET, wy + TEMP_OFFSET, TEMP_HIGH_FREQ);
        return low * 0.6 + high * 0.4;
    }

    /**
     * 湿度采样 [0,1]（FBM 多层）
     */
    public double sampleHumidity (float wx, float wy) {
        double low  = this.noise.getNorNoise(wx + HUMID_OFFSET, wy + HUMID_OFFSET, HUMID_LOW_FREQ);
        double high = this.noise.getNorNoise(wx + HUMID_OFFSET, wy + HUMID_OFFSET, HUMID_HIGH_FREQ);
        return low * 0.6 + high * 0.4;
    }

    private static double clamp01 (double v) {
        return v < 0 ? 0 : (v > 1 ? 1 : v);
    }

    /**
     * 陆地群系查表：区块级平滑高度分段 + 连续生态强度场（温度 + 湿度）
     * <p>
     * 低地（湿地）、高地（山地）由高度得分，生态群系（雪原/沙漠/森林/平原）由
     * 各自声明的温度/湿度 range 配置（matchDegree）匹配度竞争，取最大者。
     * 相邻群系在交界处由强度相对大小自然渐变切换，避免硬切。
     */
    public Biome lookupLandBiome (double temp, double humid, int chunkHeight) {
        //高度驱动的群系（绝对门槛，优先于生态群系）
        double wetland  = wetlandStrength(chunkHeight, humid);
        double mountain = clamp01((chunkHeight - 200) / 40.0);  //200起，力度到240满分
        //温度湿度配置驱动的特殊生态群系（按各群系 range 匹配度）
        double snowy  = Biomes.SNOWY.matchDegree(temp, humid);
        double desert = Biomes.DESERT.matchDegree(temp, humid);
        double forest = Biomes.FOREST.matchDegree(temp, humid);

        //① 湿地/山地按其自身强度达标优先，不与生态群系争
        if (wetland >= 0.5 && wetland >= mountain) return Biomes.WETLAND;
        if (mountain >= 0.5) return Biomes.MOUNTAIN;
        //② 特殊生态群系（雪原/沙漠/森林）取匹配最高者；都不明显（<0.6）→平原兜底
        double best = Math.max(snowy, Math.max(desert, forest));
        if (best < 0.6) return Biomes.PLAINS;
        if (best == snowy) return Biomes.SNOWY;
        if (best == desert) return Biomes.DESERT;
        return Biomes.FOREST;
    }

    /**
     * 连续"湿地强度" [0,1]（低海拔且湿润 → 接近 1）。
     * <p>
     * 高度从 180 向下渐变（跨度 20，聚焦近海低地），湿度升高增强；
     * 强度随高度/湿度连续变化，使湿地与相邻群系（平原/森林）边界渐变过渡而非硬切。
     */
    public double wetlandStrength (int chunkHeight, double humid) {
        double h = clamp01((180 - chunkHeight) / 20.0);          //180→0, 160→1（跨20，聚焦近海低地）
        double w = 0.45 + 0.55 * clamp01((humid - 0.50) / 0.25); //湿润增强，湿润低地占优
        return h * w;
    }

}
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
    private static final float HUMID_LOW_FREQ = 0.0006f, HUMID_HIGH_FREQ = 0.002f, HUMID_OFFSET = 100f;
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

    /**
     * 连续"沙漠强度" [0,1]：温度越高、湿度越低 → 越接近 1（纯沙漠）。
     * <p>
     * 用连续插值而非硬阈值，配合噪声抖动让沙漠与其他群系（尤其草原）的边界渐变过渡、斑驳自然，
     * 避免"区块级全沙 vs 全草"的硬切。阈值与 {@link #lookupLandBiome} 的 DESERT 判定一致
     * （temp≥0.65 且 humid≤0.4 附近为沙漠核心）。
     */
    public double desertStrength (float wx, float wy) {
        double temp  = this.sampleTemp(wx, wy);
        double humid = this.sampleHumidity(wx, wy);
        //温度：0.55 → 0，0.72 → 1（核心 0.65 时 ≈0.59）
        double t = clamp01((temp - 0.55) / (0.72 - 0.55));
        //湿度：0.50 → 0，0.28 → 1（核心 0.40 时 ≈0.45）
        double h = clamp01((0.50 - humid) / (0.50 - 0.28));
        return Math.min(t, h);
    }

    private static double clamp01 (double v) {
        return v < 0 ? 0 : (v > 1 ? 1 : v);
    }

    /**
     * 陆地群系查表：区块级平滑高度分段 + 连续生态强度场（温度 + 湿度）
     * <p>
     * 低地、高地不再用硬高度门槛切分，而是并入统一的<b>连续生态强度场</b>：
     * 湿地靠"低海拔 + 湿润"得分、山地靠"高海拔"得分、雪原/沙漠/森林/平原靠温度湿度得分，
     * 取各群系强度最大者。如此相邻群系在交界处由强度相对大小自然渐变切换，避免硬切。
     */
    public Biome lookupLandBiome (double temp, double humid, int chunkHeight) {
        //连续生态强度（0~1），边界由强度相对大小自然切换
        double wetland = wetlandStrength(chunkHeight, humid);                    //低海拔且湿润 → 湿地
        double mountain = clamp01((chunkHeight - Chunk.HIGH_BAND_BOTTOM) / 30.0); //高海拔 → 山地
        double snowy  = clamp01((0.45 - temp) / 0.25);                          //温度越低越强（雪原）
        double desert = clamp01((temp - 0.50) / 0.20)                           //高温主导
                        * (0.6 + 0.4 * clamp01((0.58 - humid) / 0.35));         //低湿加强，高湿减弱
        double forest = clamp01((humid - 0.55) / 0.20);                         //湿度越高越强（森林）
        double plains = 0.34 * clamp01((0.45 - Math.abs(temp - 0.5)) / 0.45);   //温湿适中的平原兜底偏置

        //取强度最大者（湿地、山地在交界处与生态群系自然竞争）
        if (wetland >= mountain && wetland >= snowy && wetland >= desert && wetland >= forest && wetland >= plains) return Biomes.WETLAND;
        if (mountain >= snowy && mountain >= desert && mountain >= forest && mountain >= plains) return Biomes.MOUNTAIN;
        if (snowy >= desert && snowy >= forest && snowy >= plains) return Biomes.SNOWY;
        if (desert >= forest && desert >= plains) return Biomes.DESERT;
        if (forest >= plains) return Biomes.FOREST;
        return Biomes.PLAINS;
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
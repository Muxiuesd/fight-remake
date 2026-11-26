package ttk.muxiuesd.world.biome;

import com.badlogic.gdx.graphics.Color;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.DataResult;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.chunk.Chunk;

import java.util.List;

/**
 * 群系
 * <p>
 * 描述一片区域的生态特征：由哪些方块构成（按高度分层）、海陆属性、环境色调。
 * 区块生成时通过海陆(Voronoi)+温度湿度+区块总体高度确定群系归属，
 * 再由群系按格子高度决定具体方块。
 */
public class Biome {
    /**
     * 群系的现代化编解码器
     * <p>
     * 群系是注册表单例，序列化时只存 id；反序列化时由 id 反查注册表拿单例（不新建实例）。
     */
    public static final Codec<Biome> CODEC = new Codec<>() {
        @Override
        public RawObject encode (Biome biome) {
            return RawObject.ofString(biome.getId().getID());
        }

        @Override
        public DataResult<Biome> decode (RawObject input) {
            DataResult<String> idResult = Codec.STRING.decode(input);
            if (!idResult.isSuccess()) {
                return DataResult.error("群系id解码失败: " + idResult.error().orElse(""));
            }
            Biome biome = byId(idResult.result().get());
            if (biome == null) {
                return DataResult.error("未知群系id: " + idResult.result().get());
            }
            return DataResult.success(biome);
        }
    };

    private Identifier id;
    private final boolean ocean;                    //是否海洋（Voronoi 海陆用）
    private float[] temperatureRange;               //温度区间 [min,max]（中海拔查表）
    private float[] humidityRange;                  //湿度区间 [min,max]
    private final Block surfaceBlock;               //兜底地表方块
    private final Block waterBlock;                 //水域方块（高度<海平面）
    private final List<BiomeBand> bands;            //高度>海平面的断点分带表（null=用默认模板）
    private final float spawnWeight;                //海陆权重（仅 OCEAN 有意义）
    private final Color tint;                       //环境色调（可选）
    private final boolean canSpawn;                 //该群系是否可作为玩家出生点（注册阶段确定）

    /**
     * 全局默认高程带模板（平原/森林/山地通用）：海平面之上 沙→草→石→雪。
     * <p>
     * 断点语义：高度 ≥ startHeight 用该方块，延续到下一断点。
     * 群系若未显式指定 {@code bands}，则沿用此默认模板。
     */
    public static final List<BiomeBand> DEFAULT_BANDS = List.of(
        BiomeBand.of(Chunk.SEA_LEVEL,         Blocks.SAND),    //150 沙（沙滩）
        BiomeBand.of(Chunk.SEA_LEVEL + 7,     Blocks.GRASS),   //157 草
        BiomeBand.of(Chunk.GRASS_TOP + 1,     Blocks.STONE),   //201 石
        BiomeBand.of(Chunk.SNOWLINE,          Blocks.SNOW)     //236 雪
    );

    public Biome (Builder builder) {
        this.id = builder.id;
        this.ocean = builder.ocean;
        this.temperatureRange = builder.temperatureRange;
        this.humidityRange = builder.humidityRange;
        this.surfaceBlock = builder.surfaceBlock;
        this.waterBlock = builder.waterBlock;
        this.bands = builder.bands;
        this.spawnWeight = builder.spawnWeight;
        this.tint = builder.tint;
        //未显式设置时：陆地群系默认可出生，海洋不可出生
        this.canSpawn = builder.canSpawnSet ? builder.canSpawn : !builder.ocean;
    }

    /**
     * 按格子的实际高度决定该格生成哪种方块。
     * <p>
     * 高度 < 海平面(SEA_LEVEL) → 水；否则在群系的高度断点分带表中定位方块，
     * 未命中任何带（理论上不会）兜底返回地表方块。
     */
    public Block decideBlock (int height) {
        if (height < Chunk.SEA_LEVEL) return this.getWaterBlock();
        List<BiomeBand> b = this.bands != null ? this.bands : DEFAULT_BANDS;
        //b 按 startHeight 升序；从高到低找第一个 height≥start
        for (int i = b.size() - 1; i >= 0; i--) {
            if (height >= b.get(i).getStartHeight()) {
                return b.get(i).getBlock();
            }
        }
        return this.getSurfaceBlock();
    }

    public boolean isOcean () {
        return this.ocean;
    }

    public Biome setTemperatureRange (float min, float max) {
        this.temperatureRange = new float[]{min, max};
        return this;
    }

    public float getTemperatureMin () {
        return this.temperatureRange == null ? 0f : this.temperatureRange[0];
    }

    public float getTemperatureMax () {
        return this.temperatureRange == null ? 1f : this.temperatureRange[1];
    }

    public Biome setHumidityRange (float min, float max) {
        this.humidityRange = new float[]{min, max};
        return this;
    }

    public float getHumidityMin () {
        return this.humidityRange == null ? 0f : this.humidityRange[0];
    }

    public float getHumidityMax () {
        return this.humidityRange == null ? 1f : this.humidityRange[1];
    }

    /**
     * 群系对该点温度湿度的匹配度 [0,1]（配置驱动）。
     * <p>
     * 温度/湿度落在本群系声明范围内 → 匹配 1（饱和）；越偏离范围匹配越低。
     * 未声明范围的维度视为全匹配（1）。两维度匹配度相乘作为综合匹配度。
     */
    public double matchDegree (double temp, double humid) {
        return this.rangeMatch(temp, this.temperatureRange)
             * this.rangeMatch(humid, this.humidityRange);
    }

    private static double rangeMatch (double value, float[] range) {
        if (range == null) return 1;                     //未声明范围 → 全匹配
        double min = range[0], max = range[1];
        if (value >= min && value <= max) return 1;      //区间内 → 满配（饱和）
        double half = (max - min) / 2.0;
        if (half <= 0) return 0;
        //区间外 → 按超出半宽的倍数线性衰减
        double d = value < min ? (min - value) / half : (value - max) / half;
        return d >= 2 ? 0 : 1 - d / 2.0;
    }

    public Identifier getId () {
        return this.id;
    }

    /**
     * 设置群系 id（注册时由 Biomes 调用，使 id 字段与注册表键一致）
     */
    public Biome setId (Identifier id) {
        this.id = id;
        return this;
    }

    /**
     * 由群系 id 反查注册表拿到对应的群系单例（未知 id 返回 null）
     */
    public static Biome byId (String id) {
        return Registries.BIOME.getOrNull(id);
    }

    public Block getSurfaceBlock () {
        return surfaceBlock;
    }

    public Block getWaterBlock () {
        return waterBlock;
    }

    public float getSpawnWeight () {
        return spawnWeight;
    }

    public Color getTint () {
        return tint;
    }

    /**
     * 该群系是否可作为玩家出生点（注册阶段确定）
     */
    public boolean getCanSpawn () {
        return canSpawn;
    }

    public static Builder builder () {
        return new Builder();
    }

    /**
     * 群系构建器
     */
    public static class Builder {
        private Identifier id;
        private boolean ocean = false;
        private float[] temperatureRange;
        private float[] humidityRange;
        private Block surfaceBlock;
        private Block waterBlock;
        private List<BiomeBand> bands;           //null=用默认模板
        private float spawnWeight = 0f;
        private Color tint;
        private boolean canSpawn;
        private boolean canSpawnSet = false;

        public Builder id (Identifier id) {
            this.id = id;
            return this;
        }

        public Builder ocean (boolean ocean) {
            this.ocean = ocean;
            return this;
        }

        public Builder temperature (float min, float max) {
            this.temperatureRange = new float[]{min, max};
            return this;
        }

        public Builder humidity (float min, float max) {
            this.humidityRange = new float[]{min, max};
            return this;
        }

        public Builder surface (Block block) {
            this.surfaceBlock = block;
            return this;
        }

        public Builder water (Block block) {
            this.waterBlock = block;
            return this;
        }

        /**
         * 显式设置该群系的高度断点分带表（null/不调用 → 用 {@link Biome#DEFAULT_BANDS} 默认模板）。
         * 断点须按 startHeight 升序。
         */
        public Builder bands (BiomeBand... bandsArr) {
            this.bands = bandsArr != null && bandsArr.length > 0 ? new java.util.ArrayList<>(java.util.List.of(bandsArr)) : null;
            return this;
        }

        public Builder spawnWeight (float spawnWeight) {
            this.spawnWeight = spawnWeight;
            return this;
        }

        public Builder tint (Color tint) {
            this.tint = tint;
            return this;
        }

        /**
         * 显式设置该群系是否可作为玩家出生点（不调用则陆地默认可、海洋默认不可）
         */
        public Builder canSpawn (boolean canSpawn) {
            this.canSpawn = canSpawn;
            this.canSpawnSet = true;
            return this;
        }

        public Biome build () {
            return new Biome(this);
        }
    }
}
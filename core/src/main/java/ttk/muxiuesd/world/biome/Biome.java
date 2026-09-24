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
    private final Block waterBlock;                 //水域方块（高度<0）
    private final Block beachBlock;                 //过渡带方块（+1~+4）
    private final List<HeightLayer> heightLayers;   //高程分层（高度带→方块）
    private final float spawnWeight;                //海陆权重（仅 OCEAN 有意义）
    private final Color tint;                       //环境色调（可选）
    private final boolean canSpawn;                 //该群系是否可作为玩家出生点（注册阶段确定）

    public Biome (Builder builder) {
        this.id = builder.id;
        this.ocean = builder.ocean;
        this.temperatureRange = builder.temperatureRange;
        this.humidityRange = builder.humidityRange;
        this.surfaceBlock = builder.surfaceBlock;
        this.waterBlock = builder.waterBlock;
        this.beachBlock = builder.beachBlock;
        this.heightLayers = builder.heightLayers;
        this.spawnWeight = builder.spawnWeight;
        this.tint = builder.tint;
        //未显式设置时：陆地群系默认可出生，海洋不可出生
        this.canSpawn = builder.canSpawnSet ? builder.canSpawn : !builder.ocean;
    }

    /**
     * 按格子的实际高度决定该格生成哪种方块（过渡流畅的核心）
     * <p>
     * 高度 < 海平面(SEA_LEVEL) → 水；海平面~沙滩带上限(BEACH_MAX) → 沙。
     * 陆地统一高程带（所有群系默认一致，保证跨群系过渡流畅）：
     *   BEACH_MAX~GRASS_TOP → 草；GRASS_TOP~STONE_TOP → 石；≥SNOWLINE → 雪。
     * 群系可通过 {@code heightLayers} 覆盖某些高度带（如沙漠全沙、雪原全雪），
     * 未命中的高度带仍回落到统一高程带。
     */
    public Block decideBlock (int height) {
        if (height < Chunk.SEA_LEVEL) return this.getWaterBlock();
        if (height <= Chunk.BEACH_MAX) return this.getBeachBlock();
        //群系覆盖层（可选）：命中某覆盖高度带则用覆盖块，否则继续走统一高程带
        if (this.getHeightLayers() != null) {
            for (HeightLayer layer : this.getHeightLayers()) {
                if (height >= layer.getMinHeight() && height <= layer.getMaxHeight()) {
                    return layer.getBlock();
                }
            }
        }
        //统一高程带：草 → 石 → 雪（雪线 ≥ SNOWLINE）
        if (height < Chunk.SNOWLINE) {
            if (height <= Chunk.GRASS_TOP) return Blocks.GRASS;
            return Blocks.STONE;
        }
        return Blocks.SNOW;
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

    public Block getBeachBlock () {
        return beachBlock;
    }

    public List<HeightLayer> getHeightLayers () {
        return heightLayers;
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
        private Block beachBlock;
        private List<HeightLayer> heightLayers;
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

        public Builder beach (Block block) {
            this.beachBlock = block;
            return this;
        }

        public Builder heightLayers (HeightLayer... layers) {
            this.heightLayers = new java.util.ArrayList<>();
            java.util.Collections.addAll(this.heightLayers, layers);
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
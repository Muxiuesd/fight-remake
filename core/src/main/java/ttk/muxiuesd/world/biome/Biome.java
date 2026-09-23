package ttk.muxiuesd.world.biome;

import com.badlogic.gdx.graphics.Color;
import ttk.muxiuesd.id.Identifier;
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
    private final Identifier id;
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
     * 按格子高度决定该格生成哪种方块
     * <p>
     * 高度 <0 → 水；位于过渡带（0~BEACH_MAX）→ 沙；否则查群系的高程分层表；最后兜底地表方块
     */
    public Block decideBlock (int height) {
        if (height < Chunk.SEA_LEVEL) return this.getWaterBlock();
        if (height <= Chunk.BEACH_MAX) return this.getBeachBlock();
        if (this.getHeightLayers() != null) {
            for (HeightLayer layer : this.getHeightLayers()) {
                if (height >= layer.getMinHeight() && height <= layer.getMaxHeight()) {
                    return layer.getBlock();
                }
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

    public Identifier getId () {
        return this.id;
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
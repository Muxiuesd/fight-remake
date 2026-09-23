package ttk.muxiuesd.registry;

import com.badlogic.gdx.graphics.Color;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.biome.Biome;
import ttk.muxiuesd.world.biome.HeightLayer;

/**
 * 所有群系的注册
 * <p>
 * OCEAN 由 Voronoi 海陆概率权重判定；其余陆地群系由"区块总体高度 + 温度 + 湿度"查表归属。
 */
public final class Biomes {
    public static void init () {
        Log.print(Biomes.class.getName(), "所有群系注册完成");
    }

    /// 海洋
    public static final Biome OCEAN = register("ocean", Biome.builder()
        .ocean(true)
        .water(Blocks.WATER)
        .spawnWeight(0.7f)
        .tint(new Color(0.2f, 0.4f, 0.9f, 1f))
        .build());

    /// 低海拔（区块总体高度 ≤ CHUNK_LOW_TOP）→ 湿地
    public static final Biome WETLAND = register("wetland", Biome.builder()
        .water(Blocks.WATER).beach(Blocks.SAND).surface(Blocks.GRASS)
        .heightLayers(
            HeightLayer.of(1, 16, Blocks.SAND),
            HeightLayer.of(17, 48, Blocks.GRASS))
        .tint(new Color(0.4f, 0.5f, 0.4f, 1f))
        .build());

    /// 高海拔（区块总体高度 ≥ CHUNK_HIGH_BOTTOM）→ 山地
    public static final Biome MOUNTAIN = register("mountain", Biome.builder()
        .water(Blocks.WATER).beach(Blocks.SAND).surface(Blocks.STONE)
        .heightLayers(
            HeightLayer.of(1, 60, Blocks.STONE),
            HeightLayer.of(61, 128, Blocks.SNOW))
        .tint(new Color(0.6f, 0.6f, 0.65f, 1f))
        .build());

    /// 中海拔（温度+湿度查表）
    public static final Biome SNOWY = register("snowy", Biome.builder()
        .temperature(0f, 0.35f)
        .water(Blocks.WATER).beach(Blocks.SAND).surface(Blocks.SNOW)
        .heightLayers(
            HeightLayer.of(5, 100, Blocks.SNOW),
            HeightLayer.of(101, 128, Blocks.STONE))
        .tint(new Color(0.9f, 0.9f, 0.95f, 1f))
        .build());

    public static final Biome DESERT = register("desert", Biome.builder()
        .temperature(0.65f, 1f).humidity(0f, 0.4f)
        .water(Blocks.WATER).beach(Blocks.SAND).surface(Blocks.SAND)
        .heightLayers(HeightLayer.of(1, 128, Blocks.SAND))
        .tint(new Color(0.85f, 0.8f, 0.5f, 1f))
        .build());

    public static final Biome FOREST = register("forest", Biome.builder()
        .temperature(0.35f, 0.75f).humidity(0.6f, 1f)
        .water(Blocks.WATER).beach(Blocks.SAND).surface(Blocks.GRASS)
        .heightLayers(
            HeightLayer.of(5, 70, Blocks.GRASS),
            HeightLayer.of(71, 128, Blocks.STONE))
        .tint(new Color(0.2f, 0.5f, 0.2f, 1f))
        .build());

    public static final Biome PLAINS = register("plains", Biome.builder()
        .temperature(0.35f, 0.75f).humidity(0f, 0.6f)
        .water(Blocks.WATER).beach(Blocks.SAND).surface(Blocks.GRASS)
        .heightLayers(
            HeightLayer.of(5, 60, Blocks.GRASS),
            HeightLayer.of(61, 128, Blocks.STONE))
        .tint(new Color(0.4f, 0.7f, 0.3f, 1f))
        .build());

    /**
     * 最基础的群系注册
     */
    public static Biome register (String name, Biome biome) {
        return register(Identifier.of(Fight.ID(name)), biome);
    }

    public static Biome register (Identifier identifier, Biome biome) {
        Registries.BIOME.register(identifier, biome);
        return biome;
    }
}
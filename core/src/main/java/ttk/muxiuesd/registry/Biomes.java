package ttk.muxiuesd.registry;

import com.badlogic.gdx.graphics.Color;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.biome.Biome;
import ttk.muxiuesd.world.biome.HeightLayer;
import ttk.muxiuesd.world.chunk.Chunk;

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

    /// 低海拔（区块平滑高度 < LOW_BAND_TOP）→ 湿地（走统一高程带，低处自然多为草地）
    public static final Biome WETLAND = register("wetland", Biome.builder()
        .water(Blocks.WATER).beach(Blocks.SAND).surface(Blocks.GRASS)
        .tint(new Color(0.4f, 0.5f, 0.4f, 1f))
        .build());

    /// 高海拔（区块平滑高度 ≥ HIGH_BAND_BOTTOM）→ 山地（走统一高程带：石→雪）
    public static final Biome MOUNTAIN = register("mountain", Biome.builder()
        .water(Blocks.WATER).beach(Blocks.SAND).surface(Blocks.STONE)
        .tint(new Color(0.6f, 0.6f, 0.65f, 1f))
        .build());

    /// 中海拔低温 → 雪原（走统一高程带，高处自然出雪；沙漠同理由生成器铺沙）
    public static final Biome SNOWY = register("snowy", Biome.builder()
        .temperature(0f, 0.35f)
        .water(Blocks.WATER).beach(Blocks.SAND).surface(Blocks.SNOW)
        .tint(new Color(0.9f, 0.9f, 0.95f, 1f))
        .build());

    /// 沙漠（方块由 desertStrength 连续渐变铺设，走统一带保高程分层与平滑过渡）
    public static final Biome DESERT = register("desert", Biome.builder()
        .temperature(0.65f, 1f).humidity(0f, 0.4f)
        .water(Blocks.WATER).beach(Blocks.SAND).surface(Blocks.SAND)
        .tint(new Color(0.85f, 0.8f, 0.5f, 1f))
        .build());

    /// 森林（走统一高程带：草→石→雪）
    public static final Biome FOREST = register("forest", Biome.builder()
        .temperature(0.35f, 0.75f).humidity(0.6f, 1f)
        .water(Blocks.WATER).beach(Blocks.SAND).surface(Blocks.GRASS)
        .tint(new Color(0.2f, 0.5f, 0.2f, 1f))
        .build());

    /// 平原（走统一高程带：草→石→雪）
    public static final Biome PLAINS = register("plains", Biome.builder()
        .temperature(0.35f, 0.75f).humidity(0f, 0.6f)
        .water(Blocks.WATER).beach(Blocks.SAND).surface(Blocks.GRASS)
        .tint(new Color(0.4f, 0.7f, 0.3f, 1f))
        .build());

    /**
     * 最基础的群系注册
     */
    public static Biome register (String name, Biome biome) {
        return register(Identifier.of(Fight.ID(name)), biome);
    }

    public static Biome register (Identifier identifier, Biome biome) {
        biome.setId(identifier);
        Registries.BIOME.register(identifier, biome);
        return biome;
    }
}

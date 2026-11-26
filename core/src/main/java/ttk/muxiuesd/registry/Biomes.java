package ttk.muxiuesd.registry;

import com.badlogic.gdx.graphics.Color;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.biome.Biome;
import ttk.muxiuesd.world.biome.BiomeBand;
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

    /// 湿地（低海拔+湿润）：低位草带，无石头/雪
    public static final Biome WETLAND = register("wetland", Biome.builder()
        .water(Blocks.WATER).surface(Blocks.GRASS)
        .bands(
            BiomeBand.of(Chunk.SEA_LEVEL, Blocks.SAND),
            BiomeBand.of(Chunk.SEA_LEVEL + 7, Blocks.GRASS))
        .tint(new Color(0.4f, 0.5f, 0.4f, 1f))
        .build());

    /// 山地/山丘（高海拔）：绝大部分石头，少部分极高的地方是雪，山脚有沙过渡
    public static final Biome MOUNTAIN = register("mountain", Biome.builder()
        .water(Blocks.WATER).surface(Blocks.STONE)
        .bands(
            BiomeBand.of(Chunk.SEA_LEVEL, Blocks.SAND),      //150 山脚沙（海岸过渡）
            BiomeBand.of(Chunk.SEA_LEVEL + 7, Blocks.STONE), //157 大部分石头
            BiomeBand.of(244, Blocks.SNOW))                  //244 极高峰才雪（少部分高处）
        .tint(new Color(0.6f, 0.6f, 0.65f, 1f))
        .build());

    /// 雪原（明显低温）：全雪
    public static final Biome SNOWY = register("snowy", Biome.builder()
        .temperature(0f, 0.30f)
        .water(Blocks.WATER).surface(Blocks.SNOW)
        .bands(BiomeBand.of(Chunk.SEA_LEVEL, Blocks.SNOW))
        .tint(new Color(0.9f, 0.9f, 0.95f, 1f))
        .build());

    /// 沙漠（明显高温+干旱）：全沙
    public static final Biome DESERT = register("desert", Biome.builder()
        .temperature(0.70f, 1f).humidity(0f, 0.30f)
        .water(Blocks.WATER).surface(Blocks.SAND)
        .bands(BiomeBand.of(Chunk.SEA_LEVEL, Blocks.SAND))
        .tint(new Color(0.85f, 0.8f, 0.5f, 1f))
        .build());

    /// 森林（明显湿润）：走默认模板
    public static final Biome FOREST = register("forest", Biome.builder()
        .temperature(0.35f, 0.75f).humidity(0.75f, 1f)
        .water(Blocks.WATER).surface(Blocks.GRASS)
        .tint(new Color(0.2f, 0.5f, 0.2f, 1f))
        .build());

    /// 平原（温和）：绝大部分草地，少部分高处是石头
    public static final Biome PLAINS = register("plains", Biome.builder()
        .temperature(0.35f, 0.75f).humidity(0f, 0.6f)
        .water(Blocks.WATER).surface(Blocks.GRASS)
        .bands(
            BiomeBand.of(Chunk.SEA_LEVEL, Blocks.SAND),      //150 沙滩（海岸过渡）
            BiomeBand.of(Chunk.SEA_LEVEL + 7, Blocks.GRASS), //157 绝大部分草地
            BiomeBand.of(228, Blocks.STONE))                 //228 高处少量石头
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

package ttk.muxiuesd.world.chunk;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.registry.Walls;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.world.biome.Biome;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.instance.BlockWater;
import ttk.muxiuesd.world.chunk.abs.ChunkGenerator;
import ttk.muxiuesd.world.wall.Wall;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 主世界区块生成器
 * <p>
 * 区块生成流程：先由真实噪声逐格生成地形高度 [0,256]；区块级海陆归属用"3×3 平滑高度"与海平面(150)
 * 比较判定（非 Voronoi）；陆地具体群系按"区块平滑高度分段 + 温度 + 湿度"查表；
 * 最后每个格子用该区块群系按该格实际高度决定方块（150 以下水、150~156 沙滩、更高按统一高程带），海陆交界自然过渡。
 */
public class MainWorldChunkGenerator extends ChunkGenerator {
    public MainWorldChunkGenerator (ChunkSystem chunkSystem) {
        super(chunkSystem);
    }

    @Override
    public Chunk generate (ChunkPosition chunkPosition) {
        ChunkSystem cs = getChunkSystem();
        int chunkX = chunkPosition.x;
        int chunkY = chunkPosition.y;
        //区块中心世界坐标（用于定位区块与采样）
        float centerX = chunkX * Chunk.ChunkWidth + Chunk.ChunkWidth / 2f;
        float centerY = chunkY * Chunk.ChunkHeight + Chunk.ChunkHeight / 2f;

        //① 区块级海陆归属：3×3 平滑高度 < 海平面 → 海洋
        int smoothedHeight = cs.smoothedChunkHeight(chunkX, chunkY);
        boolean ocean = smoothedHeight < Chunk.SEA_LEVEL;
        //陆地具体群系：区块平滑高度分段 + 温度湿度（预先算好，供本区块所有格子共用）
        double temp  = cs.sampleTemp(centerX, centerY);
        double humid = cs.sampleHumidity(centerX, centerY);
        Biome chunkBiome = ocean ? cs.getOceanBiome() : cs.lookupLandBiome(temp, humid, smoothedHeight);

        Chunk chunk = new Chunk(cs);
        chunk.setChunkPosition(chunkPosition);
        chunk.setBiome(chunkBiome);
        chunk.setCanSpawn(chunkBiome.getCanSpawn());   //出生能力来自群系注册阶段确定

        //② 遍历区块内每个格子：按该格实际高度决定方块（海陆交界由 150 等高线逐格过渡）
        chunk.traversal((x, y) -> {
            float wx = chunk.getWorldX(x);
            float wy = chunk.getWorldY(y);
            int height = cs.sampleHeight(wx, wy);       //逐格纯噪声高度 [0,256]
            //河流/湖泊（陆地内）压成水下（高度压到海平面以下深处）
            if (cs.isRiverCell(wx, wy)) height = Chunk.SEA_LEVEL - 30;
            else if (cs.isLakeCell(wx, wy)) height = Chunk.SEA_LEVEL - 35;
            //湿地：用连续湿地强度（低海拔+湿润）控制水塘，边缘水塘随强度渐少，
            //而非区块级硬判定，保证湿地与相邻群系边界的水塘渐变过渡。
            else if (cs.wetlandStrength(wx, wy) > 0.5 && cs.isWetlandPondCell(wx, wy)) height = Chunk.SEA_LEVEL - 8;

            //方块决定：统一用陆地群系查表（参数取区块平滑高度带）按该格高度 decideBlock，
            //decideBlock 在群系自己的高度断点分带表（或默认模板）中定位方块：
            //<海平面→水、海平面以上→沙/草/石/雪（按群系配置）。
            //沙漠(all-sand)、雪原(all-snow)等由各自 bands 决定，边界靠连续强度场渐变。
            Block block = cs.lookupLandBiome(temp, humid, smoothedHeight).decideBlock(height);
            chunk.setBlock(block, x, y);
            chunk.setHeight(x, y, height);
        });

        //③ 墙体（水上不生成，其余地格低概率）
        chunk.traversal((x, y) -> {
            if (chunk.getBlock(x, y) instanceof BlockWater) return;
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
        return null;
    }
}
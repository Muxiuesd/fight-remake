package ttk.muxiuesd.world.chunk;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.registry.Walls;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.instance.BlockWater;
import ttk.muxiuesd.world.chunk.abs.ChunkGenerator;
import ttk.muxiuesd.world.wall.Wall;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 主世界区块生成器
 * */
public class MainWorldChunkGenerator extends ChunkGenerator {
    public MainWorldChunkGenerator (ChunkSystem chunkSystem) {
        super(chunkSystem);
    }

    @Override
    public Chunk generate (ChunkPosition chunkPosition) {
        ChunkSystem cs = getChunkSystem();
        Chunk chunk = new Chunk(cs);
        chunk.setChunkPosition(chunkPosition);
        chunk.traversal((x, y) -> {
            //计算方块相在世界中的坐标
            float wx = chunk.getWorldX(x);
            float wy = chunk.getWorldY(y);
            int height = generateTerrain(wx, wy);
            String blockId = this.chooseBlock(height);
            ConcurrentHashMap<String, Block> blockInstancesMap = cs.getBlockInstancesMap();
            if (!blockInstancesMap.containsKey(blockId)) {
                //如果区块里不存在这个id的方块实例，就加进来
                blockInstancesMap.put(blockId, Gets.BLOCK(blockId));
            }
            Block block = blockInstancesMap.get(blockId);
            chunk.setBlock(block, x, y);
            chunk.setHeight(x, y, height);

        });

        chunk.traversal((x, y) -> {
            //水上不生成墙体
            if (chunk.getBlock(x, y) instanceof BlockWater) {
                return;
            }
            int random = MathUtils.random(0, 15);
            if (random < 1) {
                Wall<?> wall = Walls.SMOOTH_STONE.createSelf(new Vector2(chunk.getWorldX(x), chunk.getWorldY(y)));
                chunk.setWall(wall, x, y);
            }
        });

        return chunk;
    }

    @Override
    public String chooseBlock (int height) {
        String blockId = Fight.ID("block_test");
        switch (height) {
            case 0:
            case 1:
            case 2:{
                blockId = Fight.ID("water");
                break;
            }
            case 3:{
                blockId = Fight.ID("sand");
                break;
            }
            case 4:{
                blockId = Fight.ID("stone");
                break;
            }
            case 5:
            case 6:
            case 7:{
                blockId = Fight.ID("grass");
                break;
            }
        }
        return blockId;
    }
}

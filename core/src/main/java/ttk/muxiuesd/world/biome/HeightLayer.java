package ttk.muxiuesd.world.biome;

import ttk.muxiuesd.world.block.abs.Block;

/**
 * 高程分层
 * <p>
 * 群系内一个"高度带 → 方块"的映射。区块格子高度落在该带的格子生成对应方块。
 */
public class HeightLayer {
    private final int minHeight;
    private final int maxHeight;
    private final Block block;

    public HeightLayer (int minHeight, int maxHeight, Block block) {
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
        this.block = block;
    }

    public static HeightLayer of (int minHeight, int maxHeight, Block block) {
        return new HeightLayer(minHeight, maxHeight, block);
    }

    public int getMinHeight () {
        return minHeight;
    }

    public int getMaxHeight () {
        return maxHeight;
    }

    public Block getBlock () {
        return block;
    }
}
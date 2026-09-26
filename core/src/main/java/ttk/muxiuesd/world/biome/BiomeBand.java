package ttk.muxiuesd.world.biome;

import ttk.muxiuesd.world.block.abs.Block;

/**
 * 高度断点带
 * <p>
 * 语义：高度 ≥ {@code startHeight} 的地格使用 {@code block}，一直延续到下一个断点。
 * 一组断点按 startHeight 升序排列后，天然无缝、不重叠、不空洞。
 */
public class BiomeBand {
    private final int startHeight;
    private final Block block;

    public BiomeBand (int startHeight, Block block) {
        this.startHeight = startHeight;
        this.block = block;
    }

    public static BiomeBand of (int startHeight, Block block) {
        return new BiomeBand(startHeight, block);
    }

    public int getStartHeight () {
        return this.startHeight;
    }

    public Block getBlock () {
        return this.block;
    }
}
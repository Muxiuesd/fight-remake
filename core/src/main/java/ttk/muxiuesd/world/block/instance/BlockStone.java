package ttk.muxiuesd.world.block.instance;


import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 石头方块
 * */
public class BlockStone extends Block {
    public BlockStone() {
        super(createProperty(),
            Fight.ID("stone"),
            Fight.BlockTexturePath("stone.png"));
    }
}

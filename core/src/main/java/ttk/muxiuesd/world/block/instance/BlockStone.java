package ttk.muxiuesd.world.block.instance;


import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 石头方块
 * */
public class BlockStone extends Block {
    public BlockStone() {
        super(new Property(),
            Fight.getId("stone"),
            Fight.BlockTexturePath("stone.png"));
    }
}

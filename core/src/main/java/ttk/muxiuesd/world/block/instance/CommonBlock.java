package ttk.muxiuesd.world.block.instance;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 普通方块的模板
 * */
public class CommonBlock extends Block {
    public CommonBlock (String name) {
        super(new Property(), Fight.getId(name), Fight.BlockTexturePath(name + ".png"));
    }
}

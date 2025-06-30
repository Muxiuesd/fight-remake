package ttk.muxiuesd.world.block.instance;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.block.abs.Block;

public class BlockGlass extends Block {
    public BlockGlass () {
        super(new Property(), Fight.getId("glass"), Fight.BlockTexturePath("glass.png"));
    }
}

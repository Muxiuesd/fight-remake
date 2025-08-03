package ttk.muxiuesd.world.block.instance;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.block.abs.Block;

public class BlockTest extends Block {
    public BlockTest() {
        super(createProperty(),
            Fight.getId("block_test"),
            Fight.BlockTexturePath("block_test.png"));
    }
}

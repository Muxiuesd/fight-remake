package ttk.muxiuesd.world.block.instance;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 沙子方块
 * */
public class BlockSand extends Block {
    public BlockSand() {
        super(createProperty()
                .setFriction(0.95f)
                .setSounds(Sounds.SAND),
            Fight.ID("sand"),
            Fight.BlockTexturePath("sand.png"));
    }
}

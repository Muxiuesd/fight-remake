package ttk.muxiuesd.world.block.instance;

import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 草方块
 * */
public class BlockGrass extends Block {
    public BlockGrass() {
        super(
            createProperty().setFriction(1.1f).setSounds(Sounds.GRASS)
        );
    }
}

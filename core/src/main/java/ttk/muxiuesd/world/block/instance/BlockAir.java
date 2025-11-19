package ttk.muxiuesd.world.block.instance;

import com.badlogic.gdx.graphics.g2d.Batch;
import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 空气方块，就是啥也没有
 * */
public class BlockAir extends Block {
    public static final BlockRenderer<BlockAir> RENDERER = (batch, block, context) -> {};

    public BlockAir () {
        super(createProperty());
    }

    @Override
    public void draw (Batch batch, float x, float y) {
    }
}

package ttk.muxiuesd.world.block.instance;

import com.badlogic.gdx.graphics.g2d.Batch;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 空气方块，就是啥也没有
 * */
public class BlockAir extends Block {
    public BlockAir () {
        super(createProperty());
    }

    @Override
    public void draw (Batch batch, float x, float y) {
    }
}

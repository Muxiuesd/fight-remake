package ttk.muxiuesd.world.block.instance;

import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 空气方块，就是啥也没有
 * <p>
 * 在游戏中用于当作占位符的存在
 * */
public class BlockAir extends Block {
    public static final BlockRenderer<BlockAir> RENDERER = (batch, block, context) -> {};

    public BlockAir () {
        super(new Property().setFriction(0f));
    }
}

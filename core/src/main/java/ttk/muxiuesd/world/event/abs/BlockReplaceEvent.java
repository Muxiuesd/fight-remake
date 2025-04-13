package ttk.muxiuesd.world.event.abs;

import ttk.muxiuesd.interfaces.Event;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 方块替换事件
 * */
public abstract class BlockReplaceEvent implements Event {
    public void call (Object... args) {
        this.callback((World) args[0], (Block) args[1], (Block) args[2], (float) args[3], (float) args[4]);
    }

    public abstract void callback (World world, Block newBlock, Block oldBlock, float wx, float wy);
}

package ttk.muxiuesd.world.event.abs;

import ttk.muxiuesd.interfaces.Event;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 方块替换事件
 * */
public abstract class BlockReplaceEvent implements Event {
    public void call (Object... args) {
        this.callback((Block) args[0], (Block) args[1], (float) args[2], (float) args[3]);
    }

    public abstract void callback (Block newBlock, Block oldBlock, float wx, float wy);
}

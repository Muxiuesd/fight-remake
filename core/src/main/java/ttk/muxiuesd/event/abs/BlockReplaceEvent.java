package ttk.muxiuesd.event.abs;

import ttk.muxiuesd.event.Event;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;

public abstract class BlockReplaceEvent implements Event {
    public abstract void handle(World world, Block newBlock, Block oldBlock, float wx, float wy);
}

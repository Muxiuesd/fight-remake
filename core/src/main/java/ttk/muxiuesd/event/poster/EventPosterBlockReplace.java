package ttk.muxiuesd.event.poster;

import ttk.muxiuesd.event.EventPoster;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;

public class EventPosterBlockReplace extends EventPoster {
    public final World world;
    public final Block newBlock;
    public final Block oldBlock;
    public final float wx;
    public final float wy;

    public EventPosterBlockReplace(World world, Block newBlock, Block oldBlock, float wx, float wy) {
        this.world = world;
        this.newBlock = newBlock;
        this.oldBlock = oldBlock;
        this.wx = wx;
        this.wy = wy;
    }
}

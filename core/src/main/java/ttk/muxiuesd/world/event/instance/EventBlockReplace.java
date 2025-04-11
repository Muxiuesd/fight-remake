package ttk.muxiuesd.world.event.instance;

import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.event.abs.BlockReplaceEvent;

public class EventBlockReplace extends BlockReplaceEvent {
    @Override
    public void callback (Block newBlock, Block oldBlock, float wx, float wy) {
        Log.print("EventBlockReplace", "放置：" + newBlock + wx + " , " + wy);
    }
}

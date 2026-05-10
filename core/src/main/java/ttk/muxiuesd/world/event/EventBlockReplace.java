package ttk.muxiuesd.world.event;

import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.event.abs.BlockReplaceEvent;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockSounds;
import ttk.muxiuesd.world.block.abs.Block;

public class EventBlockReplace extends BlockReplaceEvent {
    @Override
    public void handle (World world, Block newBlock, Block oldBlock, float wx, float wy) {
        String id = oldBlock.getProperty().getSounds().getTypeID(BlockSounds.Type.DESTROY);
        AudioPlayer.getInstance().playMusic(id);
    }
}

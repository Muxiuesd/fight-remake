package ttk.muxiuesd.world.event;

import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.event.abs.BlockReplaceEvent;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockSoundsID;
import ttk.muxiuesd.world.block.abs.Block;

public class EventBlockReplace extends BlockReplaceEvent {
    @Override
    public void handle (World world, Block newBlock, Block oldBlock, float wx, float wy) {
        //SoundEffectSystem ses = (SoundEffectSystem) world.getSystemManager().getSystem("SoundEffectSystem");
        String id = oldBlock.getProperty().getSounds().getID(BlockSoundsID.Type.DESTROY);
        AudioPlayer.getInstance().playMusic(id);
    }
}

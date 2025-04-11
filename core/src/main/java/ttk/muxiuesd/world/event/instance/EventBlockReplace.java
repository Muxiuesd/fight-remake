package ttk.muxiuesd.world.event.instance;

import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockSoundsID;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.event.abs.BlockReplaceEvent;

public class EventBlockReplace extends BlockReplaceEvent {
    @Override
    public void callback (World world, Block newBlock, Block oldBlock, float wx, float wy) {
        //Log.print("EventBlockReplace", "放置：" + newBlock + wx + " , " + wy);
        SoundEffectSystem ses = (SoundEffectSystem) world.getSystemManager().getSystem("SoundEffectSystem");
        String id = oldBlock.getProperty().getSounds().getID(BlockSoundsID.Type.PUT);
        AudioPlayer.getInstance().playMusic(id);
    }
}

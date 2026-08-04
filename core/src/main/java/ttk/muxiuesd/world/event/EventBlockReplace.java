package ttk.muxiuesd.world.event;

import com.badlogic.gdx.math.Vector3;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.event.abs.BlockReplaceEvent;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;

public class EventBlockReplace extends BlockReplaceEvent {
    @Override
    public void handle (World world, Block newBlock, Block oldBlock, float wx, float wy) {
        AudioHolder destroySound = oldBlock.getProperty().getSounds().destroy();
        Vector3 pos = new Vector3(wx, wy, 0);
        world.getSystem(SoundSystem.class).playSpatialSound(destroySound, () -> pos);
    }
}

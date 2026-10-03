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
        //损坏存档（未知方块id）解码失败时该格会保持 null，破坏这种格子没有旧方块可发声
        if (oldBlock == null) return;
        AudioHolder destroySound = oldBlock.getProperty().getSounds().destroy();
        Vector3 pos = new Vector3(wx, wy, 0);
        world.getSystem(SoundSystem.class).playSpatialSound(destroySound, () -> pos);
    }
}

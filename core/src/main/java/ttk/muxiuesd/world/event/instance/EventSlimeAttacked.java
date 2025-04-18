package ttk.muxiuesd.world.event.instance;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.enemy.Slime;
import ttk.muxiuesd.world.event.abs.EntityAttackedEvent;

/**
 * 史莱姆受到攻击
 * */
public class EventSlimeAttacked extends EntityAttackedEvent {
    private World world;

    public EventSlimeAttacked (World world) {
        this.world = world;
    }

    @Override
    public void callback (Entity attackObject, Entity victim) {
        if (victim instanceof Slime) {
            //AudioPlayer.getInstance().playMusic(Fight.getId("hurt_3"));
            SoundEffectSystem ses = (SoundEffectSystem) world
                .getSystemManager()
                .getSystem("SoundEffectSystem");
            ses.newSpatialSound(Fight.getId("hurt_3"), victim);
        }
    }
}

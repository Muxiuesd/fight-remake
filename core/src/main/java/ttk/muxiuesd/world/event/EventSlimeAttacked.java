package ttk.muxiuesd.world.event;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.event.abs.EntityHurtEvent;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.enemy.Slime;

/**
 * 史莱姆受到攻击
 * */
public class EventSlimeAttacked extends EntityHurtEvent {
    /*@Override
    public void callback (Entity attackObject, Entity victim) {
        if (victim instanceof Slime) {
            //AudioPlayer.getInstance().playMusic(Fight.getId("hurt_3"));
            SoundEffectSystem ses = (SoundEffectSystem) world
                .getSystemManager()
                .getSystem("SoundEffectSystem");
            ses.newSpatialSound(Fight.getId("hurt_3"), victim);
        }
    }*/

    @Override
    public void handle (World world, Entity attackObject, Entity victim) {
        if (victim instanceof Slime) {
            //AudioPlayer.getInstance().playMusic(Fight.getId("hurt_3"));
            SoundEffectSystem ses = (SoundEffectSystem) world
                .getSystemManager()
                .getSystem("SoundEffectSystem");
            ses.newSpatialSound(Fight.getId("hurt_3"), victim);
        }
    }
}

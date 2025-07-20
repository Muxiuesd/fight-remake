package ttk.muxiuesd.world.event;

import ttk.muxiuesd.event.abs.EntityHurtEvent;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 敌人受到攻击
 * */
public class EventEnemyAttacked extends EntityHurtEvent {

    @Override
    public void handle (World world, Entity<?> attackObject, Entity<?> victim) {
        //播放收到攻击的音效
        SoundEffectSystem ses = (SoundEffectSystem) world
            .getSystemManager()
            .getSystem("SoundEffectSystem");
        ses.newSpatialSound(Sounds.ENTITY_HURT_3, victim);
    }
}

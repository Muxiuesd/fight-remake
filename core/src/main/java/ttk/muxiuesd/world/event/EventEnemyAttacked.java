package ttk.muxiuesd.world.event;

import ttk.muxiuesd.event.abs.EntityHurtEvent;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 敌人受到攻击
 * */
public class EventEnemyAttacked extends EntityHurtEvent {

    @Override
    public void handle (World world, Entity<?> attackObject, Entity<?> victim) {
        //播放收到攻击的音效
        SoundSystem ses = world.getSystem(SoundSystem.class);
        ses.playSpatialSound(Sounds.ENTITY_HURT_3, victim);
    }
}

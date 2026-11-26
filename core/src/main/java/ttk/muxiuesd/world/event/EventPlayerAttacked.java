package ttk.muxiuesd.world.event;

import ttk.muxiuesd.event.abs.EntityHurtEvent;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.player.Player;

/**
 * 玩家受到攻击
 * */
public class EventPlayerAttacked extends EntityHurtEvent {

    @Override
    public void handle (World world, Entity<?> attackObject, Entity<?> victim) {
        if (victim instanceof Player) {
            world.getSystem(SoundSystem.class).playSpatialSound(Sounds.ENTITY_HURT_1, victim);
        }
    }
}

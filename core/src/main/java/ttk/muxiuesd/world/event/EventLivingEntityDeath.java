package ttk.muxiuesd.world.event;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.event.abs.LivingEntityDeathEvent;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

/**
 * 活体生物死亡事件
 * */
public class EventLivingEntityDeath extends LivingEntityDeathEvent {
    @Override
    public void handle (World world, LivingEntity<?> entity) {
        entity.onDeath(world);
        world.getSystem(SoundSystem.class).playSpatialSound(Sounds.PLAYER_KILL, entity);
        Log.print(this.getClass().getName(), entity + " 死亡");
    }
}

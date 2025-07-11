package ttk.muxiuesd.world.event;

import ttk.muxiuesd.event.abs.LivingEntityDeathEvent;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

/**
 * 活体生物死亡事件
 * */
public class EventLivingEntityDeath extends LivingEntityDeathEvent {
    @Override
    public void handle (World world, LivingEntity entity) {
        entity.onDeath(world);
    }
}

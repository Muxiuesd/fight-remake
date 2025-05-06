package ttk.muxiuesd.event.abs;

import ttk.muxiuesd.event.Event;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

public abstract class EntityDeathEvent implements Event {
    public abstract void handle (World world, LivingEntity entity);
}

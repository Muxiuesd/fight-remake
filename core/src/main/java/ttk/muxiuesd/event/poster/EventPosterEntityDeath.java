package ttk.muxiuesd.event.poster;

import ttk.muxiuesd.event.EventPoster;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

public class EventPosterEntityDeath extends EventPoster {
    public final World world;
    public final LivingEntity entity;

    public EventPosterEntityDeath (World world, LivingEntity entity) {
        this.world = world;
        this.entity = entity;
    }
}

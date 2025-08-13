package ttk.muxiuesd.event.poster;

import ttk.muxiuesd.event.EventPoster;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Entity;

public class EventPosterEntityHurt extends EventPoster {
    public final World world;
    public final Entity<?> attackObject;
    public final Entity<?> victim;

    public EventPosterEntityHurt (World world, Entity<?> attackObject, Entity<?> victim) {
        this.world = world;
        this.attackObject = attackObject;
        this.victim = victim;
    }
}

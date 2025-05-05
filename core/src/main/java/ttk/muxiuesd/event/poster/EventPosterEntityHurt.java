package ttk.muxiuesd.event.poster;

import ttk.muxiuesd.event.EventPoster;
import ttk.muxiuesd.world.entity.abs.Entity;

public class EventPosterEntityHurt extends EventPoster {
    public final Entity attackObject;
    public final Entity victim;

    public EventPosterEntityHurt (Entity attackObject, Entity victim) {
        this.attackObject = attackObject;
        this.victim = victim;
    }
}

package ttk.muxiuesd.event.abs;

import ttk.muxiuesd.event.Event;
import ttk.muxiuesd.world.entity.abs.Entity;

public abstract class EntityHurtEvent implements Event {
    public abstract void handle (Entity attackObject, Entity victim);
}

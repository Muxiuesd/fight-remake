package ttk.muxiuesd.event.abs;

import ttk.muxiuesd.event.Event;
import ttk.muxiuesd.world.World;

public abstract class WorldTickEvent implements Event{
    public abstract void tick (World world, float delta);
}

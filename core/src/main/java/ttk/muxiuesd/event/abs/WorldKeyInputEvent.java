package ttk.muxiuesd.event.abs;

import ttk.muxiuesd.event.Event;
import ttk.muxiuesd.world.World;

public abstract class WorldKeyInputEvent implements Event {
    public abstract void process(World world, int key);
}

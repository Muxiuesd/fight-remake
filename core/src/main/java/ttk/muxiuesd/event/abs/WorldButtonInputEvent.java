package ttk.muxiuesd.event.abs;

import ttk.muxiuesd.event.Event;
import ttk.muxiuesd.world.World;

public abstract class WorldButtonInputEvent implements Event {
    public abstract void process(World world, int screenX, int screenY, int pointer, int button);
}

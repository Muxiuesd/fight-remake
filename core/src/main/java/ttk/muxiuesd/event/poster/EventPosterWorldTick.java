package ttk.muxiuesd.event.poster;

import ttk.muxiuesd.event.EventPoster;
import ttk.muxiuesd.world.World;

public class EventPosterWorldTick extends EventPoster {
    public final World world;
    public final float delta;

    public EventPosterWorldTick(World world, float delta) {
        this.world = world;
        this.delta = delta;
    }
}

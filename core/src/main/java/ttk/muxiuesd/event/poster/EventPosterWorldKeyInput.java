package ttk.muxiuesd.event.poster;

import ttk.muxiuesd.event.EventPoster;
import ttk.muxiuesd.world.World;

public class EventPosterWorldKeyInput extends EventPoster {
    public final World world;
    public final int key;

    public EventPosterWorldKeyInput (World world, int key) {
        this.world = world;
        this.key = key;
    }
}

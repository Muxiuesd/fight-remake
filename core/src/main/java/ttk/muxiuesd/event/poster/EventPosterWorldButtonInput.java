package ttk.muxiuesd.event.poster;

import ttk.muxiuesd.event.EventPoster;
import ttk.muxiuesd.world.World;

public class EventPosterWorldButtonInput extends EventPoster {
    public final World world;
    public final int screenX;
    public final int screenY;
    public final int pointer;
    public final int button;

    public EventPosterWorldButtonInput (World world, int screenX, int screenY, int pointer, int button) {
        this.world = world;
        this.screenX = screenX;
        this.screenY = screenY;
        this.pointer = pointer;
        this.button = button;
    }
}

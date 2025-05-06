package ttk.muxiuesd.event.poster;

import ttk.muxiuesd.event.EventPoster;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Player;

public class EventPosterPlayerDeath extends EventPoster {
    public final World world;
    public final Player player;

    public EventPosterPlayerDeath(World world, Player player) {
        this.world = world;
        this.player = player;
    }
}

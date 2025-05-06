package ttk.muxiuesd.event.abs;

import ttk.muxiuesd.event.Event;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Player;

public abstract class PlayerDeathEvent implements Event {
    public abstract void handle (World world, Player player);
}

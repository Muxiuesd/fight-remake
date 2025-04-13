package ttk.muxiuesd.world.event.abs;

import ttk.muxiuesd.interfaces.Event;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Player;

public abstract class PlayerDeathEvent implements Event {
    @Override
    public void call (Object... args) {
        this.callback((World) args[0], (Player) args[1]);
    }
    /**
     * @param world 玩家死亡时所在的世界
     * @param player 玩家实体
     * */
    public abstract void callback (World world, Player player);
}

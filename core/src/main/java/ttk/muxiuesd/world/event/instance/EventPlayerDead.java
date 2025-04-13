package ttk.muxiuesd.world.event.instance;

import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.event.abs.PlayerDeathEvent;

/**
 * 玩家死亡事件
 * */
public class EventPlayerDead extends PlayerDeathEvent {
    public final String TAG = this.getClass().getName();

    @Override
    public void callback (World world, Player player) {
        Log.print(TAG, "位于世界："+ world +" 的玩家" + player + "死亡");
    }
}

package ttk.muxiuesd.world.event;

import ttk.muxiuesd.event.abs.PlayerDeathEvent;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Player;

/**
 * 玩家死亡事件
 * */
public class EventPlayerDead extends PlayerDeathEvent {
    public final String TAG = this.getClass().getName();

    @Override
    public void handle (World world, Player player) {
        Log.print(TAG, "位于世界："+ world +" 的玩家" + player + "死亡");
    }

}

package ttk.muxiuesd.world.event.instance;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.event.abs.EntityAttackedEvent;

/**
 * 玩家受到攻击
 * */
public class EventPlayerAttacked extends EntityAttackedEvent {
    @Override
    public void call (Entity attackObject, Entity victim) {
        if (victim instanceof Player) {
            Player player = (Player) victim;
            AudioPlayer.getInstance().playMusic(Fight.getId("hurt_1"));
        }
    }
}

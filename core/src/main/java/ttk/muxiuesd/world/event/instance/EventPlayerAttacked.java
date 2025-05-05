package ttk.muxiuesd.world.event.instance;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.event.abs.EntityHurtEvent;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 玩家受到攻击
 * */
public class EventPlayerAttacked extends EntityHurtEvent {
    /*@Override
    public void callback (Entity attackObject, Entity victim) {
        if (victim instanceof Player) {
            Player player = (Player) victim;
            AudioPlayer.getInstance().playMusic(Fight.getId("hurt_1"));
        }
    }*/

    @Override
    public void handle (Entity attackObject, Entity victim) {
        if (victim instanceof Player) {
            Player player = (Player) victim;
            AudioPlayer.getInstance().playMusic(Fight.getId("hurt_1"));
        }
    }
}

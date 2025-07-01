package ttk.muxiuesd.world.event;

import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.event.abs.EntityHurtEvent;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 玩家受到攻击
 * */
public class EventPlayerAttacked extends EntityHurtEvent {

    @Override
    public void handle (World world, Entity attackObject, Entity victim) {
        if (victim instanceof Player) {
            Player player = (Player) victim;
            AudioPlayer.getInstance().playMusic(Sounds.ENTITY_HURT_1);
        }
    }
}

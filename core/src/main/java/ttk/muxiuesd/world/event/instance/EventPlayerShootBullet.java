package ttk.muxiuesd.world.event.instance;

import com.badlogic.gdx.audio.Sound;
import ttk.muxiuesd.audio.AudioLoader;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.bullet.Bullet;
import ttk.muxiuesd.world.event.abs.BulletShootEvent;

/**
 * 事件：玩家发射子弹
 * */
public class EventPlayerShootBullet extends BulletShootEvent {
    @Override
    public void call (Entity shooter, Bullet bullet) {
        if (shooter.group == Group.player) {
            SoundEffectSystem soundEffectSystem = (SoundEffectSystem) shooter
                .getEntitySystem()
                .getWorld()
                .getSystemManager()
                .getSystem("SoundEffectSystem");
            Sound sound = AudioLoader.getInstance().getSound("shoot");
            soundEffectSystem.newSpatialSound(sound, bullet);
        }
    }
}

package ttk.muxiuesd.world.event.instance;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.bullet.Bullet;
import ttk.muxiuesd.world.event.abs.BulletShootEvent;

/**
 * 事件：玩家发射子弹
 * */
public class EventPlayerShootBullet extends BulletShootEvent {
    private final World world;

    public EventPlayerShootBullet (World world) {
        this.world = world;
    }

    @Override
    public void call (Entity shooter, Bullet bullet) {
        if (shooter.group == Group.player) {
            SoundEffectSystem ses = (SoundEffectSystem) world
                .getSystemManager()
                .getSystem("SoundEffectSystem");
            ses.newSpatialSound(Fight.getId("shoot"), bullet);
        }
    }
}

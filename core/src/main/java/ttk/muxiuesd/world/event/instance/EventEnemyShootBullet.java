package ttk.muxiuesd.world.event.instance;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.bullet.Bullet;
import ttk.muxiuesd.world.event.abs.BulletShootEvent;

/**
 * 事件：敌人发射子弹
 * */
public class EventEnemyShootBullet extends BulletShootEvent {
    private final World world;

    public EventEnemyShootBullet (World world) {
        this.world = world;
    }

    @Override
    public void call (Entity shooter, Bullet bullet) {
        if (shooter.group == Group.enemy) {
            SoundEffectSystem ses = (SoundEffectSystem) world
                .getSystemManager()
                .getSystem("SoundEffectSystem");
            //TODO 不同子弹不同音效，不同实体不同音效
            ses.newSpatialSound(Fight.getId("shoot"), bullet);
        }
    }
}

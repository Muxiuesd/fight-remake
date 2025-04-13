package ttk.muxiuesd.world.event.instance;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.ParticleSystem;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.bullet.Bullet;
import ttk.muxiuesd.world.event.abs.BulletShootEvent;
import ttk.muxiuesd.world.particle.ParticleDefaultConfig;

/**
 * 事件：敌人发射子弹
 * */
public class EventEnemyShootBullet extends BulletShootEvent {
    private final World world;

    public EventEnemyShootBullet (World world) {
        this.world = world;
    }

    @Override
    public void callback (Entity shooter, Bullet bullet) {
        if (shooter.group == Group.enemy) {
            SoundEffectSystem ses = (SoundEffectSystem) world
                .getSystemManager()
                .getSystem("SoundEffectSystem");

            //AudioPlayer.getInstance().playSound("testmod:ignite");

            //TODO 不同子弹不同音效，不同实体不同音效
            ses.newSpatialSound(Fight.getId("shoot"), bullet);
            //ses.newSpatialSound("testmod:ignite", bullet);

            ParticleSystem pts = (ParticleSystem) world.getSystemManager().getSystem("ParticleSystem");
            pts.emitParticle(Fight.getId("enemy_shoot"), MathUtils.random(3, 5),
                bullet.getPosition(), bullet.getVelocity(),
                1.5f, ParticleDefaultConfig.ShootParticle);
        }
    }
}

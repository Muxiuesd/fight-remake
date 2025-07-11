package ttk.muxiuesd.world.event;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.event.abs.BulletShootEvent;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.ParticleSystem;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.particle.ParticleDefaultConfig;

/**
 * 事件：敌人发射子弹
 * */
public class EventEnemyShootBullet extends BulletShootEvent {
    @Override
    public void handle (World world, Entity shooter, Bullet bullet) {
        if (shooter.group == Group.enemy) {
            SoundEffectSystem ses = (SoundEffectSystem) world
                .getSystemManager()
                .getSystem("SoundEffectSystem");

            //TODO 不同子弹不同音效，不同实体不同音效
            ses.newSpatialSound(Sounds.ENTITY_SHOOT, bullet);

            ParticleSystem pts = (ParticleSystem) world.getSystemManager().getSystem("ParticleSystem");
            pts.emitParticle(Fight.getId("enemy_shoot"), MathUtils.random(3, 5),
                bullet.getPosition(), bullet.getVelocity(),
                1.5f, ParticleDefaultConfig.ShootParticle);
        }
    }
}

package ttk.muxiuesd.world.event;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.event.abs.BulletShootEvent;
import ttk.muxiuesd.registry.EntityTypes;
import ttk.muxiuesd.registry.ParticleEmitters;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.ParticleSystem;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 事件：玩家发射子弹
 * */
public class EventPlayerShootBullet extends BulletShootEvent {
    @Override
    public void handle (World world, Entity<?> shooter, Bullet bullet) {
        if (shooter.getType() == EntityTypes.PLAYER) {
            SoundSystem ses = world.getSystem(SoundSystem.class);
            ses.playSpatialSound(Sounds.ENTITY_SHOOT, bullet);

            ParticleSystem pts = world.getSystem(ParticleSystem.class);

            pts.emitParticle(ParticleEmitters.PLAYER_SHOOT,
                MathUtils.random(7, 15),
                bullet.getPosition(), bullet.getVelocity().scl(0.3f),
                bullet.getOrigin(),
                bullet.getSize().scl(0.9f), bullet.getSize().scl(0.1f),
                bullet.getScale(),
                bullet.getRotation(), bullet.getMaxLiveTime() * 0.6f);
        }
    }
}

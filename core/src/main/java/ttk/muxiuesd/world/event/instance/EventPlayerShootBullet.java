package ttk.muxiuesd.world.event.instance;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.event.abs.BulletShootEvent;
import ttk.muxiuesd.system.ParticleSystem;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 事件：玩家发射子弹
 * */
public class EventPlayerShootBullet extends BulletShootEvent {
    private final World world;

    public EventPlayerShootBullet (World world) {
        this.world = world;
    }

    /*@Override
    public void callback (Entity shooter, Bullet bullet) {
        if (shooter.group == Group.player) {
            SoundEffectSystem ses = (SoundEffectSystem) world
                .getSystemManager()
                .getSystem("SoundEffectSystem");
            ses.newSpatialSound(Fight.getId("shoot"), bullet);

            ParticleSystem pts = (ParticleSystem) world.getSystemManager().getSystem("ParticleSystem");
            //Texture texture = AssetsLoader.getInstance().getById(Fight.getId("spell"), Texture.class);


            pts.emitParticle(Fight.getId("player_shoot"), MathUtils.random(7, 15),
                bullet.getPosition(), bullet.getVelocity().scl(3f),
                bullet.getOrigin(),
                bullet.getSize().scl(0.9f), bullet.getSize().scl(0.1f),
                bullet.getScale(),
                bullet.rotation, bullet.getMaxLiveTime() * 0.6f);
        }
    }*/

    @Override
    public void handle (Entity shooter, Bullet bullet) {
        if (shooter.group == Group.player) {
            SoundEffectSystem ses = (SoundEffectSystem) world
                .getSystemManager()
                .getSystem("SoundEffectSystem");
            ses.newSpatialSound(Fight.getId("shoot"), bullet);

            ParticleSystem pts = (ParticleSystem) world.getSystemManager().getSystem("ParticleSystem");

            pts.emitParticle(Fight.getId("player_shoot"), MathUtils.random(7, 15),
                bullet.getPosition(), bullet.getVelocity().scl(3f),
                bullet.getOrigin(),
                bullet.getSize().scl(0.9f), bullet.getSize().scl(0.1f),
                bullet.getScale(),
                bullet.rotation, bullet.getMaxLiveTime() * 0.6f);
        }
    }
}

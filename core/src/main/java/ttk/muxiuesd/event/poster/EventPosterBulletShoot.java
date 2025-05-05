package ttk.muxiuesd.event.poster;

import ttk.muxiuesd.event.EventPoster;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Entity;

public class EventPosterBulletShoot extends EventPoster {
    public final Entity shooter;
    public final Bullet bullet;

    public EventPosterBulletShoot (Entity shooter, Bullet bullet) {
        this.shooter = shooter;
        this.bullet = bullet;
    }
}

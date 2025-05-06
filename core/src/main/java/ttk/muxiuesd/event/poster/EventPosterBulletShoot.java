package ttk.muxiuesd.event.poster;

import ttk.muxiuesd.event.EventPoster;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Entity;

public class EventPosterBulletShoot extends EventPoster {
    public final World world;
    public final Entity shooter;
    public final Bullet bullet;

    public EventPosterBulletShoot (World world, Entity shooter, Bullet bullet) {
        this.world = world;
        this.shooter = shooter;
        this.bullet = bullet;
    }
}

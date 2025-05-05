package ttk.muxiuesd.event.world;

import ttk.muxiuesd.event.Event;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Entity;

public abstract class EventBulletShoot extends Event {
    public abstract void handle (Entity shooter, Bullet bullet);
}

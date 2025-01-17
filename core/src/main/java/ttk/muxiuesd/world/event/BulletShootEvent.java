package ttk.muxiuesd.world.event;

import ttk.muxiuesd.interfaces.Event;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.bullet.Bullet;

public abstract class BulletShootEvent implements Event {
    public abstract void call (Entity shooter, Bullet bullet);
}

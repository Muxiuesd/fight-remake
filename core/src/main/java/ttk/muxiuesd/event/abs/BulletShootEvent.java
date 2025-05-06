package ttk.muxiuesd.event.abs;

import ttk.muxiuesd.event.Event;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Entity;

public abstract class BulletShootEvent implements Event {
    public abstract void handle (World world, Entity shooter, Bullet bullet);
}

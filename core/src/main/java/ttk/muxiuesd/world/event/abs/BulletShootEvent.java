package ttk.muxiuesd.world.event.abs;

import ttk.muxiuesd.interfaces.Event;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.bullet.Bullet;

public abstract class BulletShootEvent implements Event {
    @Override
    public void call (Object... args) {
        this.callback((Entity) args[0], (Bullet) args[1]);
    }

    public abstract void callback (Entity shooter, Bullet bullet);
}

package ttk.muxiuesd.world.entity.bullet;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.Bullet;

public class BulletFire extends Bullet {
    public BulletFire (World world, EntityType<?> entityType) {
        super(world, entityType,
            Fight.getId("bullet_fire"), "bullet/flame.png",
            1f, 15f, 3f, 0f);
    }
}

package ttk.muxiuesd.world.entity.bullet;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.Bullet;

/**
 * 火焰子弹
 * */
public class BulletFire extends Bullet {
    public BulletFire (World world, EntityType<?> entityType) {
        super(
            world, entityType,
            Fight.ID("bullet_fire"), "bullet/flame.png",
            1f, 12f, 3f, 0f
        );
    }
}

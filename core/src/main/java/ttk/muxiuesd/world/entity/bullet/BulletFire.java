package ttk.muxiuesd.world.entity.bullet;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Entity;

public class BulletFire extends Bullet {
    public BulletFire () {
        super(Fight.getId("bullet_fire"), "bullet/flame.png",
            1f, 15f, 3f, 0f);
    }

    public BulletFire(Entity owner) {
        super(owner);

        setSize(0.5f, 0.5f);
        damage = 1f;
        setMaxLiveTime(3f);
        setLiveTime(0f);
        speed = 15f;
        bodyTexture = getTextureRegion(Fight.getId("bullet_fire"), "bullet/flame.png");
    }
}

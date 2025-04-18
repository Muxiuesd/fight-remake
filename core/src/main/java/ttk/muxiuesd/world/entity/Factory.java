package ttk.muxiuesd.world.entity;

import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.bullet.Bullet;
import ttk.muxiuesd.world.entity.bullet.BulletFire;

// 创建实体工具
public final class Factory {

    private Factory() {}

    public static Bullet createBullet(Entity from, Direction direction) {
        BulletFire bullet = new BulletFire(from);
        bullet.setPosition(from.x + (from.width - bullet.width) / 2, from.y + (from.height - bullet.height) / 2);
        bullet.setDirection(direction.getxDirection(), direction.getyDirection());
        bullet.setCullingArea(bullet.x, bullet.y, bullet.width, bullet.height);
        return bullet;
    }
}

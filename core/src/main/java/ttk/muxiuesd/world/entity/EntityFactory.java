package ttk.muxiuesd.world.entity;

import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.bullet.BulletFire;

// 创建实体工具
public final class EntityFactory {

    public static Bullet createFireBullet (Entity shooter, Direction direction) {
        BulletFire bullet = new BulletFire(shooter);
        bullet.setPosition(shooter.x + (shooter.width - bullet.width) / 2, shooter.y + (shooter.height - bullet.height) / 2);
        bullet.setDirection(direction.getxDirection(), direction.getyDirection());
        bullet.setCullingArea(bullet.x, bullet.y, bullet.width, bullet.height);
        return bullet;
    }
}

package ttk.muxiuesd.interfaces.world.entity;

import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 接口：子弹生成
 * */
public interface BulletFactory<T extends Bullet> {
    T create (World world, Entity owner, Direction direction);
}

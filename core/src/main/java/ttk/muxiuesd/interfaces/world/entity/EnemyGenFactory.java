package ttk.muxiuesd.interfaces.world.entity;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Enemy;

/**
 * 接口：敌人生成工厂
 * */
public interface EnemyGenFactory<T extends Enemy> extends EntityGenFactory<T> {
    T[] create (World world, float genX, float genY);
}

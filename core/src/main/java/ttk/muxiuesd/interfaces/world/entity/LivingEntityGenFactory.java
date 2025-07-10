package ttk.muxiuesd.interfaces.world.entity;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

/**
 * 最基础的活体实体工厂
 * */
public interface LivingEntityGenFactory<T extends LivingEntity> extends EntityGenFactory<T> {
    T[] create (World world, float genX, float genY);
}

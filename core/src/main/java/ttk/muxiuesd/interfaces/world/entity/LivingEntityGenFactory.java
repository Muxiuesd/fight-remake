package ttk.muxiuesd.interfaces.world.entity;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

/**
 * 最基础的活体实体工厂
 * */
public interface LivingEntityGenFactory<T extends LivingEntity<?>> extends EntityGenFactory<T> {
    /**
     * 生成实体的方法，只管实体的生成，不管添加（添加操作在实体生成系统里自动完成）
     * */
    T[] create (World world, float genX, float genY);
}

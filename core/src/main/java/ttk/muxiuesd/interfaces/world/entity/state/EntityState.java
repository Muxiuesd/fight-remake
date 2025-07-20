package ttk.muxiuesd.interfaces.world.entity.state;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 实体状态机
 * */
public interface EntityState<T extends Entity> {
    void handle (World world, T entity, float delta);
}

package ttk.muxiuesd.interfaces.world.entity.state;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

public interface LivingEntityState<T extends LivingEntity<?>> extends EntityState<T> {
    @Override
    void handle (World world, T entity, float delta);
}

package ttk.muxiuesd.interfaces.world.entity.state;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

public interface LivingEntityState<T extends LivingEntity<?>> extends EntityState<T> {
    /**
     * 开始这个状态，在结束上一个状态后，换到此状态立马调用
     * */
    void start (World world, T entity);

    /**
     * 状态执行中
     * */
    @Override
    void handle (World world, T entity, float delta);

    /**
     * 结束这个状态，在换到下一个状态前调用
     * */
    void end (World world, T entity);
}

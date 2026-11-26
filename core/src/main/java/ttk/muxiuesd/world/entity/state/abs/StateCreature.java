package ttk.muxiuesd.world.entity.state.abs;

import game.muxiuesd.bedrockcore.util.TaskTimer;
import ttk.muxiuesd.interfaces.world.entity.state.LivingEntityState;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.CreatureEntity;

/**
 * 生物实体的状态机抽象类
 */
public abstract class StateCreature<T extends CreatureEntity<?>> implements LivingEntityState<T> {
    private TaskTimer timer;

    public StateCreature<T> setTimer (TaskTimer timer) {
        this.timer = timer;
        return this;
    }

    public TaskTimer getTimer () {
        return this.timer;
    }

    /**
     * 回收计时器
     */
    public StateCreature<T> freeTimer () {
        if (this.timer != null) {
            Pools.TASK_TIMER.free(this.timer);
            this.timer = null;
        }
        return this;
    }

    @Override
    public void end (World world, T entity) {
        //默认释放计时器
        this.freeTimer();
    }
}

package ttk.muxiuesd.world.entity.state.abs;

import ttk.muxiuesd.interfaces.world.entity.state.LivingEntityState;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Enemy;

/**
 * 敌人实体的状态机
 * */
public abstract class StateEnemy<T extends Enemy<?>> implements LivingEntityState<T> {
    private TaskTimer timer;

    public StateEnemy<T> setTimer (TaskTimer timer) {
        this.timer = timer;
        return this;
    }

    public TaskTimer getTimer () {
        return this.timer;
    }

    public void updateAndCheckTimer (float delta, Runnable ifReady, Runnable ifNotReady) {
        TaskTimer taskTimer = getTimer();
        if (taskTimer != null) {
            taskTimer.update(delta);
            if (taskTimer.isReady()) {
                ifReady.run();
            }else {
                ifNotReady.run();
            }
        }
    }

    /**
     * 回收计时器
     * */
    public StateEnemy<T> freeTimer () {
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

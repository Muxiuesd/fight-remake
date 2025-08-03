package ttk.muxiuesd.world.entity.state.abs;

import ttk.muxiuesd.interfaces.world.entity.state.LivingEntityState;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.world.entity.creature.PufferFish;

/**
 * 河豚的状态机抽象类
 * */
public abstract class StatePufferFish implements LivingEntityState<PufferFish> {
    private TaskTimer timer;

    public StatePufferFish() {}
    public StatePufferFish (TaskTimer timer, Runnable task) {
        this.timer = timer;
        this.timer.setTask(task);
    }

    public TaskTimer getTimer () {
        return timer;
    }

    public StatePufferFish setTimer (TaskTimer timer) {
        this.timer = timer;
        return this;
    }

    public Runnable getTask () {
        return this.timer.getTask();
    }

    public StatePufferFish setTask (Runnable task) {
        this.timer.setTask(task);
        return this;
    }
}

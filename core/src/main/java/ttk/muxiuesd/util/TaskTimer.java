package ttk.muxiuesd.util;

/**
 * 任务计时器
 * <p>
 * 到时候自动执行某项任务
 * */
public class TaskTimer extends Timer {
    private final Runnable task;

    public TaskTimer (float maxSpan, Runnable task) {
        this(maxSpan, 0, task);
    }

    public TaskTimer (float maxSpan, float curSpan, Runnable task) {
        super(maxSpan, curSpan);
        this.task = task;
    }

    /**
     * 检查是否到时间，如果到时间时返回true并且自动归零，同时给定的执行任务
     * */
    @Override
    public boolean isReady () {
        boolean ready = super.isReady();
        //执行任务
        if (ready && task != null) this.task.run();

        return ready;
    }
}

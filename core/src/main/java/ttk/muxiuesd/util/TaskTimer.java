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

    @Override
    public boolean isReady () {
        boolean ready = super.isReady();
        //执行任务
        if (ready && task != null) this.task.run();

        return ready;
    }
}

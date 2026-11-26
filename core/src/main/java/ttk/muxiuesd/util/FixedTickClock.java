package ttk.muxiuesd.util;

/**
 * 固定节拍时钟
 * <p>
 * 把每帧不等长的现实时间折算成"这一帧应该跑几个固定长度的逻辑步"。
 * 只负责算步数，不负责调用——谁拿它驱动 tick，谁自己写循环。
 * */
public class FixedTickClock {
    /// 单帧最多补多少现实时间（秒）：切窗口、断点、加载卡顿回来后不会一次补上百步
    private static final float MAX_FRAME_DELTA = 0.25f;
    /// 单帧最多补多少个逻辑步：补不完的时间直接丢弃，宁可世界慢一点也不要卡死
    private static final int MAX_STEPS_PER_FRAME = 5;

    private final float tickSpan;
    private final float maxFrameDelta;
    private final int maxStepsPerFrame;

    private float accumulator;

    public FixedTickClock (float tickSpan) {
        this(tickSpan, MAX_FRAME_DELTA, MAX_STEPS_PER_FRAME);
    }

    public FixedTickClock (float tickSpan, float maxFrameDelta, int maxStepsPerFrame) {
        this.tickSpan = tickSpan;
        this.maxFrameDelta = maxFrameDelta;
        this.maxStepsPerFrame = maxStepsPerFrame;
    }

    /**
     * 传入上一帧的真实耗时，返回这一帧要跑的逻辑步数
     * <p>
     * 帧间隔先 clamp 到 maxFrameDelta，累加后再 clamp 到 maxStepsPerFrame × tickSpan：
     * 两步 clamp 之后仍跑不完的那部分时间会被丢弃，不会累积成"越来越快"的补帧债务。
     * */
    public int advance (float frameDelta) {
        this.accumulator += Math.min(frameDelta, this.maxFrameDelta);

        float maxAccumulator = this.maxStepsPerFrame * this.tickSpan;
        if (this.accumulator > maxAccumulator) {
            this.accumulator = maxAccumulator;
        }

        int steps = (int) (this.accumulator / this.tickSpan);
        this.accumulator -= steps * this.tickSpan;
        return steps;
    }

    /**
     * 清空累加的时间（切场景、读档完成后调用，避免把暂停期间的时间带进新场景）
     * */
    public void reset () {
        this.accumulator = 0f;
    }

    public float getTickSpan () {
        return this.tickSpan;
    }

    public float getAccumulator () {
        return this.accumulator;
    }
}

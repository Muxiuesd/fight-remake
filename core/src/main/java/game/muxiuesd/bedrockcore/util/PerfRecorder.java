package game.muxiuesd.bedrockcore.util;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Stack;

/**
 * 游戏的性能记录
 * <p>
 * 在每个方法的调用前和调用后记录这个方法所用的时间
 * */
public class PerfRecorder {
    private ConvPool<Data> dataPool;
    /// 上一次记录的数据堆栈
    private Stack<Data> dataStack;
    /// 实时更新的数据库堆栈
    private Stack<Data> curDataStack;
    /// 当前记录的数据的名称
    private String curName;
    /// 上一次时间，单位是纳秒
    private long lastTime;

    public PerfRecorder () {
        this.dataPool = new ConvPool<>(Data.class);
        this.dataStack = new Stack<>();
        this.curDataStack = new Stack<>();
    }

    /**
     * 开始记录
     * */
    public void begin () {
        //如果是空的就提前返回
        if (this.curDataStack.empty()) return;

        //如果不是空的就执行回收
        for (Data data : this.curDataStack) {
            this.dataPool.free(data);
        }
        //清理上次记录的数据
        this.curDataStack.clear();
    }

    /**
     * 结束记录
     * */
    public void end () {
        //将这次记录的数据全部加入
        this.dataStack.addAll(this.curDataStack);
    }

    /**
     * 开始一个记录
     * */
    public void start (String name) {
        this.curName = name;
        this.lastTime = this.getCurNanoTime();
    }

    /**
     * 结束一次记录
     * */
    public void stop () {
        Data data = this.dataPool.obtain()
            .setName(this.curName)
            .setCostTime(TimeUtils.nanosToMillis(this.getCurNanoTime() - this.lastTime));
        this.curDataStack.push(data);

        this.curName = null;
    }

    /**
     * 获取当前的时间
     * */
    public long getCurNanoTime () {
        return TimeUtils.nanoTime();
    }

    public Stack<Data> getDataStack () {
        return this.dataStack;
    }

    public PerfRecorder setDataStack (Stack<Data> dataStack) {
        this.dataStack = dataStack;
        return this;
    }

    public Stack<Data> getCurDataStack () {
        return this.curDataStack;
    }

    public PerfRecorder setCurDataStack (Stack<Data> curDataStack) {
        this.curDataStack = curDataStack;
        return this;
    }

    /**
     * 数据
     * */
    public static class Data implements Pool.Poolable {
        private String name;
        /// 花费时间，单位是毫秒
        private float costTime;

        public Data setName (String name) {
            this.name = name;
            return this;
        }

        public Data setCostTime (float costTime) {
            this.costTime = costTime;
            return this;
        }

        public String getName () {
            return this.name;
        }

        public float getCostTime () {
            return this.costTime;
        }

        @Override
        public void reset () {
            this.name = null;
            this.costTime = 0f;
        }
    }
}

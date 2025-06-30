package ttk.muxiuesd.util;

import ttk.muxiuesd.interfaces.Updateable;

/**
 * 计时器,用于各种需要检查时间间隔的地方
 * */
public class Timer implements Updateable{
    private float maxSpan;
    private float curSpan;

    public Timer (float maxSpan) {
        this(maxSpan, 0);
    }

    public Timer (float maxSpan, float curSpan) {
        this.maxSpan = maxSpan;
        this.curSpan = curSpan;
    }

    /**
     * 检查是否到时间，如果到时间时返回true并且自动归零
     * */
    public boolean isReady () {
        if (this.curSpan == this.maxSpan) {
            //到时间时返回true并且自动归零
            this.curSpan = 0;
            return true;
        }
        return false;
    }

    @Override
    public void update (float delta) {
        this.curSpan += delta;
        if (this.curSpan > this.maxSpan) this.curSpan = this.maxSpan;
    }

    public float getMaxSpan () {
        return maxSpan;
    }

    public void setMaxSpan (float maxSpan) {
        this.maxSpan = maxSpan;
    }

    public float getCurSpan () {
        return curSpan;
    }

    public void setCurSpan (float curSpan) {
        this.curSpan = curSpan;
    }
}

package ttk.muxiuesd.interfaces;

/**
 * 游戏底层固定频率的Tick更新接口
 * */
public interface FixedTickable {
    /**
     * @param tickSpan 距离上一帧的时间间隔
     * */
    void tick (float tickSpan);
}

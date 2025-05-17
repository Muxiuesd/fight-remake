package ttk.muxiuesd.interfaces;

import ttk.muxiuesd.world.World;

/**
 * 能够tick更新的类解释此接口
 * <p>
 * 非每帧更新，间隔一定的时间的更新
 * */
public interface Tickable {
    /**
     * @param delta 距离上次tick所间隔的时间
     * */
    void tick(World world, float delta);
}

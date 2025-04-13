package ttk.muxiuesd.world.event.abs;

import ttk.muxiuesd.interfaces.Event;
import ttk.muxiuesd.world.World;

/**
 * 世界tick更新事件
 * */
public abstract class WorldTickUpdateEvent implements Event {
    @Override
    public void call (Object... args) {
        this.tick((World) args[0], (Float) args[1]);
    }
    /**
     * @param world 进行tick更新的世界
     * @param delta 距离上一次tick的更新间隔
     * */
    abstract public void tick (World world, float delta);
}

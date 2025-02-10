package ttk.muxiuesd.system.abs;

import ttk.muxiuesd.world.World;

/**
 * 运行在世界里的系统
 * */
public abstract class WorldSystem extends FightSystem {
    private World world;

    public WorldSystem(World world) {
        this.world = world;
        setManager(world.getSystemManager());
    }

    /**
     * 延迟初始化
     * */
    public void initialize () {

    }

    public World getWorld() {
        return world;
    }
}

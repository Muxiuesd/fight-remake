package ttk.muxiuesd.system.manager;

import ttk.muxiuesd.system.abs.SystemManager;
import ttk.muxiuesd.world.World;

/**
 * 世界的系统管理
 * */
public class WorldSystemsManager extends SystemManager {
    private final World world;

    public WorldSystemsManager(World world) {
        this.world = world;
    }

    public World getWorld() {
        return this.world;
    }
}

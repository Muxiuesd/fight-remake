package ttk.muxiuesd.mod.api;

import ttk.muxiuesd.world.World;

/**
 * modAPI：提供当前的游戏世界
 * */
public class ModWorldProvider {
    private static World curWorld;

    public static World getWorld () {
        return curWorld;
    }

    public static void setCurWorld (World world) {
        curWorld = world;
    }
}

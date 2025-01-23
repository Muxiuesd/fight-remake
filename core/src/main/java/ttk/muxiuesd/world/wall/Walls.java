package ttk.muxiuesd.world.wall;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;

/**
 * 墙体注册
 * TODO 注册mod墙体
 * */
public class Walls {
    static Registrant<Wall> registrant = RegistrantGroup.getRegistrant(Fight.NAMESPACE, Wall.class);
    static {
        register("wall_test", new WallTest());
        register("wall_smooth_stone", new WallSmoothStone());
    }
    private static void register (String id, Wall wall) {
        registrant.register(id, wall);
    }

    public static Wall newWall (String id) {
        return registrant.get(id);
    }
}

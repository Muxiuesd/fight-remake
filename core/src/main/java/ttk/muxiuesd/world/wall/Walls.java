package ttk.muxiuesd.world.wall;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;

import java.util.function.Supplier;

/**
 * 墙体注册
 * TODO 注册mod墙体
 * */
public class Walls {
    static Registrant<Wall> registrant = RegistrantGroup.getRegistrant(Fight.NAMESPACE, Wall.class);
    static {
        register("wall_test", WallTest::new);
        register("wall_smooth_stone", WallSmoothStone::new);
    }
    private static void register (String id, Supplier<Wall> supplier) {
        registrant.register(id, supplier);
    }

    public static Wall newWall (String id) {
        return registrant.get(id);
    }
}

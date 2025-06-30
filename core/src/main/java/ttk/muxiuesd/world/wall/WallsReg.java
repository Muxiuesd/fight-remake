package ttk.muxiuesd.world.wall;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;

import java.util.function.Supplier;

/**
 * 墙体注册
 * TODO 注册mod墙体
 * */
public class WallsReg {
    public static Registrant<Wall> registrant = RegistrantGroup.getRegistrant(Fight.NAMESPACE, Wall.class);
    public static void initAllWalls() {
    }

    public static final Wall TEST_WALL = register("wall_test", WallTest::new);
    public static final Wall SMOOTH_STONE_WALL = register("wall_smooth_stone", WallSmoothStone::new);

    private static Wall register (String id, Supplier<Wall> supplier) {
        return registrant.register(id, supplier);
    }

    public static Wall newWall (String id) {
        return registrant.get(id);
    }
}

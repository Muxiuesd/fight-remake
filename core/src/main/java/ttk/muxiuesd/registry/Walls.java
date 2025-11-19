package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.render.world.block.WallRenderer;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registrant.WallRendererRegistry;
import ttk.muxiuesd.world.wall.Wall;
import ttk.muxiuesd.world.wall.WallSmoothStone;

import java.util.function.Supplier;

public final class Walls {
    public static void init() {}

    public static final WallSmoothStone SMOOTH_STONE = register("smooth_stone", WallSmoothStone::new);

    public static <T extends Wall<?>> T register (String name, Supplier<T> supplier) {
        return register(name, supplier, new WallRenderer.StandardRenderer<>());
    }

    /**
     * 最基础的墙体注册
     * */
    public static <T extends Wall<?>> T register (String name, Supplier<T> supplier, WallRenderer<T> renderer) {
        String id = Fight.ID(name);
        T t = supplier.get();
        t.setID(id);
        Registries.WALL.register(new Identifier(id), t);
        WallRendererRegistry.register(t, renderer);
        return t;
    }
}

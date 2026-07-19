package ttk.muxiuesd.registry;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.render.world.block.WallRenderer;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registrant.WallRendererRegistry;
import ttk.muxiuesd.world.wall.Wall;
import ttk.muxiuesd.world.wall.WallSmoothStone;

import java.util.function.Supplier;

public final class Walls {
    public static void init() {
        Log.print(Walls.class.getName(), "所有墙体注册完成");
    }

    public static final WallSmoothStone SMOOTH_STONE = register("smooth_stone", WallSmoothStone::new);

    public static <T extends Wall<?>> T register (String name, Supplier<T> supplier) {
        return register(supplier, new WallRenderer.StandardRenderer<>(), Identifier.of(Fight.ID(name)));
    }

    /**
     * 最基础的墙体注册
     * */
    public static <T extends Wall<?>> T register (Supplier<T> supplier, WallRenderer<T> renderer, Identifier identifier) {
        T t = supplier.get();
        t.setIdentifier(identifier);
        Registries.WALL.register(identifier, t);
        WallRendererRegistry.register(t, renderer);
        return t;
    }
}

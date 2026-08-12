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
        return register(supplier, null, Identifier.of(Fight.ID(name)), Fight.BlockTexturePath(name + ".png"));
    }

    /**
     * 最基础的墙体注册
     * @param renderer 渲染器，为null时根据贴图路径自动创建标准渲染器
     * @param texturePath 贴图文件路径，为null时通过id从已注册的映射中获取
     * */
    public static <T extends Wall<?>> T register (Supplier<T> supplier, WallRenderer<T> renderer, Identifier identifier, String texturePath) {
        T t = supplier.get();
        t.setIdentifier(identifier);
        Registries.WALL.register(identifier, t);
        if (renderer == null) {
            renderer = new WallRenderer.StandardRenderer<>(identifier.getID(), texturePath);
        }
        WallRendererRegistry.register(t, renderer);
        return t;
    }
}

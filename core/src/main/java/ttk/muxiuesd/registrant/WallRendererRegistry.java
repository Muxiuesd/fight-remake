package ttk.muxiuesd.registrant;

import ttk.muxiuesd.interfaces.render.world.block.WallRenderer;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.wall.Wall;

import java.util.HashMap;

/**
 * 墙体渲染器的注册
 * */
public class WallRendererRegistry {
    /**
     * 静态调用
     * */
    public static <T extends Wall<?>> WallRenderer<T> register (T wall, WallRenderer<T> wallRenderer) {
        return getInstance().registerRenderer(wall, wallRenderer);
    }

    public static <T extends Wall<?>> WallRenderer<T> getRenderer (T wall) {
        WallRenderer<? extends Wall<?>> renderer = getInstance().getRenderersMap().get(wall.getID());
        if (renderer == null) {
            Log.error(WallRendererRegistry.class.getName(), "方块：" + wall.getID() + " 的渲染器不存在！！！");
        }
        return (WallRenderer<T>) renderer;
    }

    private static WallRendererRegistry INSTANCE;
    public static WallRendererRegistry getInstance () {
        if (INSTANCE == null) {
            INSTANCE = new WallRendererRegistry();
        }
        return INSTANCE;
    }

    private final HashMap<String, WallRenderer<? extends Wall<?>>> renderersMap = new HashMap<>();

    /**
     * 基础注册方法
     * @param wall 传入的方块必须持有id
     * */
    public <T extends Wall<?>> WallRenderer<T> registerRenderer (T wall, WallRenderer<T> wallRenderer) {
        if (wall != null && wallRenderer != null) {
            if (this.getRenderersMap().containsKey(wall.getID())) {
                Log.print(this.getClass().getName(),
                    "已存在过墙体：" + wall.getClass().getName() + " 的渲染器，执行覆盖！");
            }
            this.getRenderersMap().put(wall.getID(), wallRenderer);
            return wallRenderer;
        }else {
            Log.error(this.getClass().getName(), "墙体或者渲染器为null！！！");
        }
        return null;
    }

    public HashMap<String, WallRenderer<? extends Wall<?>>> getRenderersMap () {
        return this.renderersMap;
    }
}

package ttk.muxiuesd.render.abs;

import com.badlogic.gdx.graphics.Camera;
import ttk.muxiuesd.world.World;

/**
 * 游戏世界相关的渲染处理器的抽象类
 * <p>
 * 世界相关的渲染就继承这个
 * */
public abstract class WorldRenderProcessor extends RenderProcessor {
    private final World world;

    public WorldRenderProcessor (Camera camera, String shaderId, int renderOrder, World world) {
        super(camera, shaderId, renderOrder);
        this.world = world;
    }

    public World getWorld () {
        return world;
    }
}

package ttk.muxiuesd.render.abs;

import com.badlogic.gdx.graphics.Camera;
import ttk.muxiuesd.world.World;

/**
 * 世界里的各种元素渲染所用的渲染处理器
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

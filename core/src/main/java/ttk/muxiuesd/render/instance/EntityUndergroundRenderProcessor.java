package ttk.muxiuesd.render.instance;

import com.badlogic.gdx.graphics.Camera;
import ttk.muxiuesd.interfaces.render.IRenderTask;
import ttk.muxiuesd.interfaces.render.IWorldUndergroundEntityRender;
import ttk.muxiuesd.render.abs.WorldEntityRenderProcessor;
import ttk.muxiuesd.world.World;

/**
 * 地下实体渲染
 * */
public class EntityUndergroundRenderProcessor extends WorldEntityRenderProcessor {
    public EntityUndergroundRenderProcessor (Camera camera, String shaderId, int renderOrder, World world) {
        super(camera, shaderId, renderOrder, world);
    }

    @Override
    public boolean recognize (IRenderTask task) {
        if (task instanceof IWorldUndergroundEntityRender) {
            addRenderTask(task);
            return true;
        }
        return false;
    }
}

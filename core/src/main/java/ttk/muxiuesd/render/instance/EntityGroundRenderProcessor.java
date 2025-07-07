package ttk.muxiuesd.render.instance;

import com.badlogic.gdx.graphics.Camera;
import ttk.muxiuesd.interfaces.render.IRenderTask;
import ttk.muxiuesd.interfaces.render.IWorldGroundEntityRender;
import ttk.muxiuesd.render.abs.WorldEntityRenderProcessor;
import ttk.muxiuesd.world.World;

/**
 * 地面实体渲染
 * */
public class EntityGroundRenderProcessor extends WorldEntityRenderProcessor {
    public EntityGroundRenderProcessor (Camera camera, String shaderId, int renderOrder, World world) {
        super(camera, shaderId, renderOrder, world);
    }

    @Override
    public boolean recognize (IRenderTask task) {
        if (task instanceof IWorldGroundEntityRender) {
            addRenderTask(task);
            return true;
        }
        return false;
    }
}

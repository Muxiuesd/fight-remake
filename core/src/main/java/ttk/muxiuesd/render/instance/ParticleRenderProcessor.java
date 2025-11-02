package ttk.muxiuesd.render.instance;

import com.badlogic.gdx.graphics.Camera;
import ttk.muxiuesd.interfaces.render.IRenderTask;
import ttk.muxiuesd.interfaces.render.IWorldParticleRender;
import ttk.muxiuesd.render.abs.WorldRenderProcessor;
import ttk.muxiuesd.world.World;

/**
 * 粒子效果的渲染处理器
 * */
public class ParticleRenderProcessor extends WorldRenderProcessor {
    public ParticleRenderProcessor (Camera camera, String shaderId, int renderOrder, World world) {
        super(camera, shaderId, renderOrder, world);
    }

    @Override
    public boolean recognize (IRenderTask task) {
        if (task instanceof IWorldParticleRender) {
            addRenderTask(task);
            return true;
        }
        return false;
    }
}

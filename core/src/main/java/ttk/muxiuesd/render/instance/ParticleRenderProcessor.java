package ttk.muxiuesd.render.instance;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.interfaces.render.IRenderTask;
import ttk.muxiuesd.interfaces.render.IWorldParticleRender;
import ttk.muxiuesd.render.abs.WorldRenderProcessor;
import ttk.muxiuesd.world.World;

public class ParticleRenderProcessor extends WorldRenderProcessor {
    public ParticleRenderProcessor (Camera camera, String shaderId, int renderOrder, World world) {
        super(camera, shaderId, renderOrder, world);
    }

    @Override
    public void handleRender (Batch batch, ShapeRenderer shapeRenderer) {
        batch.setProjectionMatrix(getCamera().combined);
        shapeRenderer.setProjectionMatrix(getCamera().combined);
        beginShader(batch);

        getRenderTasks().forEach(task -> task.render(batch, shapeRenderer));

        endShader();
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

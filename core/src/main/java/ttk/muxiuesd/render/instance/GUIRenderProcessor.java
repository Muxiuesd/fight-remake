package ttk.muxiuesd.render.instance;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.interfaces.render.IGUIRender;
import ttk.muxiuesd.interfaces.render.IRenderTask;
import ttk.muxiuesd.render.abs.RenderProcessor;

/**
 * GUI的渲染处理器
 * */
public class GUIRenderProcessor extends RenderProcessor {
    public GUIRenderProcessor (Camera camera, String shaderId, int renderOrder) {
        super(camera, shaderId, renderOrder);
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
        if (task instanceof IGUIRender) {
            addRenderTask(task);
            return true;
        }
        return false;
    }
}

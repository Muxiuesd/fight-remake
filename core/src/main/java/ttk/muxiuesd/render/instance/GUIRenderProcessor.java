package ttk.muxiuesd.render.instance;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.interfaces.render.IGUIRender;
import ttk.muxiuesd.interfaces.render.IRenderTask;
import ttk.muxiuesd.render.abs.RenderProcessor;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.system.LightSystem;

/**
 * GUI的渲染处理器
 * */
public class GUIRenderProcessor extends RenderProcessor {
    private MainGameScreen mainGameScreen;
    public GUIRenderProcessor (Camera camera, String shaderId, int renderOrder, MainGameScreen mainGameScreen) {
        super(camera, shaderId, renderOrder);
        this.mainGameScreen = mainGameScreen;
    }

    @Override
    public void handleBatchRender (Batch batch) {
        this.mainGameScreen.getWorld().getSystem(LightSystem.class).afterProcess();

        defaultHandleBatchRender(batch);
    }

    @Override
    public void handleShapeRender (ShapeRenderer shapeRenderer) {
        defaultHandleShapeRender(shapeRenderer);
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

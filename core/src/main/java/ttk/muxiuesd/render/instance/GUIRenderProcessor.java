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
    private MainGameScreen mainGameScreen = null;
    public GUIRenderProcessor (Camera camera, String shaderId, int renderOrder) {
        super(camera, shaderId, renderOrder);
    }

    @Override
    public void handleBatchRender (Batch batch) {
        if (this.mainGameScreen != null) {
            //GUI在世界中渲染时需要后处理光照
            this.mainGameScreen.getWorld().getSystem(LightSystem.class).afterProcess();
        }


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

    public GUIRenderProcessor setMainGameScreen (MainGameScreen mainGameScreen) {
        this.mainGameScreen = mainGameScreen;
        return this;
    }
}

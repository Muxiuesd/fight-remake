package ttk.muxiuesd.render.instance;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.interfaces.render.IMenuGUIRender;
import ttk.muxiuesd.interfaces.render.IRenderTask;
import ttk.muxiuesd.render.abs.RenderProcessor;

/**
 * 游戏主菜单界面的GUI渲染处理器
 * */
public class MenuGUIRenderProcessor extends RenderProcessor {
    public MenuGUIRenderProcessor (Camera camera, String shaderId, int renderOrder) {
        super(camera, shaderId, renderOrder);
    }

    @Override
    public void handleBatchRender (Batch batch) {
        defaultHandleBatchRender(batch);
    }

    @Override
    public void handleShapeRender (ShapeRenderer shapeRenderer) {
        defaultHandleShapeRender(shapeRenderer);
    }

    @Override
    public boolean recognize (IRenderTask task) {
        if (task instanceof IMenuGUIRender) {
            addRenderTask(task);
            //return true;
        }
        return false;
    }
}

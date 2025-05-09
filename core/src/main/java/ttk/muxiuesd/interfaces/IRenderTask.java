package ttk.muxiuesd.interfaces;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * 渲染任务
 * */
public interface IRenderTask {
    void render(Batch batch, ShapeRenderer shapeRenderer);
}

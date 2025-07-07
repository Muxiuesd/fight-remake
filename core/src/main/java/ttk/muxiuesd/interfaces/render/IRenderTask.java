package ttk.muxiuesd.interfaces.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * 渲染任务
 * */
public interface IRenderTask {
    void render(Batch batch, ShapeRenderer shapeRenderer);

    /**
     * 渲染优先级，优先级值越小，渲染顺序越靠前（即 1 比 2 先渲染）
     */
    int getRenderPriority();
}

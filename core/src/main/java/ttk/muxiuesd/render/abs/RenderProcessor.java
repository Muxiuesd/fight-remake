package ttk.muxiuesd.render.abs;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.interfaces.IRenderTask;
import ttk.muxiuesd.interfaces.IRenderTaskRecognizer;
import ttk.muxiuesd.shader.ShaderScheduler;

import java.util.ArrayList;

/**
 * 渲染处理器
 * <p>
 * 每一大类游戏元素的渲染都要一个渲染处理器来完成
 * */
public abstract class RenderProcessor implements Comparable<RenderProcessor>, IRenderTaskRecognizer {
    private Camera camera;
    private String shaderId;
    private int renderOrder;
    private final ArrayList<IRenderTask> renderTasks;

    public RenderProcessor(Camera camera, String shaderId, int renderOrder) {
        this.camera = camera;
        this.shaderId = shaderId;
        this.renderOrder = renderOrder;
        this.renderTasks = new ArrayList<>();
    }

    public abstract void handleRender (Batch batch, ShapeRenderer shapeRenderer);

    protected void beginShader(Batch batch) {
        if (this.getShaderId() == null) return;
        ShaderScheduler.getInstance().begin(this.getShaderId(), batch);
    }

    protected void endShader() {
        if (this.getShaderId() == null) return;
        ShaderScheduler.getInstance().end(this.getShaderId());
    }

    public Camera getCamera () {
        return camera;
    }

    public void setCamera (Camera camera) {
        this.camera = camera;
    }

    public String getShaderId () {
        return shaderId;
    }

    public void setShaderId (String shaderId) {
        this.shaderId = shaderId;
    }

    public int getRenderOrder () {
        return renderOrder;
    }

    public void setRenderOrder (int renderOrder) {
        this.renderOrder = renderOrder;
    }

    public ArrayList<IRenderTask> getRenderTasks () {
        return renderTasks;
    }

    public void addRenderTask (IRenderTask renderTask) {
        this.renderTasks.add(renderTask);
    }

    @Override
    public int compareTo (RenderProcessor o) {
        return this.getRenderOrder();
    }
}

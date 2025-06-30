package ttk.muxiuesd.render.abs;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.interfaces.IRenderTask;
import ttk.muxiuesd.interfaces.IRenderTaskRecognizer;
import ttk.muxiuesd.shader.ShaderScheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 渲染处理器
 * <p>
 * 每一大类游戏元素的渲染都要一个渲染处理器来完成
 * */
public abstract class RenderProcessor implements Comparable<RenderProcessor>, IRenderTaskRecognizer {
    private Camera camera;      //相机
    private String shaderId;    //使用的着色器id
    private int renderOrder;    //这个渲染处理器的渲染顺序
    private final ArrayList<IRenderTask> renderTasks;

    public RenderProcessor(Camera camera, String shaderId, int renderOrder) {
        this.camera = camera;
        this.shaderId = shaderId;
        this.renderOrder = renderOrder;
        this.renderTasks = new ArrayList<>();
    }

    /**
     * 处理渲染任务
     * */
    public abstract void handleRender (Batch batch, ShapeRenderer shapeRenderer);

    /**
     * 添加渲染任务，自动排序
     * */
    public void addRenderTask (IRenderTask renderTask) {
        this.renderTasks.add(renderTask);
        this.sortRenderTasks();
    }

    /**
     * 对渲染任务进行排序
     */
    public void sortRenderTasks() {
        Collections.sort(this.getRenderTasks(), Comparator.comparingInt(IRenderTask::getRenderPriority));
    }

    /**
     * 开始着色器
     * */
    protected void beginShader(Batch batch) {
        if (this.getShaderId() == null) return;
        ShaderScheduler.getInstance().begin(this.getShaderId(), batch);
    }

    /**
     * 结束着色器
     * */
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
        return this.renderTasks;
    }

    @Override
    public int compareTo (RenderProcessor o) {
        return Integer.compare(this.getRenderOrder(), o.getRenderOrder());
    }
}

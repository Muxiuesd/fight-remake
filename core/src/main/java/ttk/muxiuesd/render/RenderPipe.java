package ttk.muxiuesd.render;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.render.camera.CameraController;
import ttk.muxiuesd.render.camera.GUICamera;
import ttk.muxiuesd.render.camera.PlayerCamera;
import ttk.muxiuesd.render.fix.GL32CMacIssueHandler;
import ttk.muxiuesd.util.Log;

import java.util.HashSet;
import java.util.Set;

/**
 * 游戏的渲染管线
 * */
public class RenderPipe {
    public static String TAG = RenderPipe.class.getName();

    //必须单例模式
    private static RenderPipe INSTANCE;

    //所有的相机控制器，管理着需要被自动更新的相机
    private final Set<CameraController> cameraControllers = new HashSet<>();
    private final Batch batch ;
    private final ShapeRenderer shapeRenderer;

    private RenderPipe() {
        this(
            GL32CMacIssueHandler.createSpriteBatch(),
            GL32CMacIssueHandler.createShapeRenderer()
        );
    }
    /// 支持注入
    private RenderPipe(Batch batch, ShapeRenderer shapeRenderer) {
        this.batch = batch;
        this.shapeRenderer = shapeRenderer;
        this.shapeRenderer.setAutoShapeType(true);

        this.addCameraController(PlayerCamera.INSTANCE);
        this.addCameraController(GUICamera.INSTANCE);
    }

    /**
     * 初始化渲染管线
     * */
    public static void init () {
        if (INSTANCE == null) {
            INSTANCE = new RenderPipe();
            Log.print(TAG, "游戏渲染管线初始化完成！");
        }else {
            Log.error(TAG, "不要重复初始化！！！");
        }
    }

    public static RenderPipe getInstance() {
        return INSTANCE;
    }

    /**
     * 处理每一帧的渲染
     * */
    public void handleGameRender() {
        //更新相机
        this.cameraControllers.forEach(controller-> controller.getCamera().update());

        RenderProcessorManager.sort();

        this.batch.begin();
        RenderProcessorManager.batchRender(batch);
        this.batch.end();

        this.shapeRenderer.begin();
        RenderProcessorManager.shapeRender(this.shapeRenderer);
        this.shapeRenderer.end();
    }

    /**
     * 添加相机控制器
     * */
    public void addCameraController(CameraController cameraController) {
        this.cameraControllers.add(cameraController);
    }

    public void resize(int width, int height) {
        this.cameraControllers.forEach(controller-> controller.resize(width, height));
    }

    public Batch getBatch () {
        return this.batch;
    }

    public ShapeRenderer getShapeRenderer () {
        return this.shapeRenderer;
    }
}

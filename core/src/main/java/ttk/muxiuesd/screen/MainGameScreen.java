package ttk.muxiuesd.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ttk.muxiuesd.audio.AudioReg;
import ttk.muxiuesd.camera.CameraController;
import ttk.muxiuesd.mod.ModLoader;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.shader.ShaderScheduler;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.MainWorld;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlocksReg;
import ttk.muxiuesd.world.entity.EntitiesReg;

/**
 * 主游戏屏幕
 * */
public class MainGameScreen implements Screen {
    public static String TAG = MainGameScreen.class.getName();

    public Batch batch = new SpriteBatch();
    public Viewport viewport;
    public CameraController cameraController;
    public ShapeRenderer shapeRenderer = new ShapeRenderer() {{
        setAutoShapeType(true);
    }};

    //游戏目前加载的世界，后续可能有多个世界
    private World world;

    @Override
    public void show() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        this.cameraController = new CameraController(new OrthographicCamera());
        this.viewport = new ScalingViewport(Scaling.fit, w, h, cameraController.camera);

        //手动注册游戏内的元素
        AudioReg.registerAllAudios();
        BlocksReg.registerAllBlocks();
        EntitiesReg.registerAllEntities();

        //初始化着色器调度器
        ShaderScheduler.init();

        this.world = new MainWorld(this);
        this.world.getSystemManager().initAllSystems();

        //执行mod代码
        ModLoader.getInstance().runAllMods();

        RegistrantGroup.printAllBlocks();
        RegistrantGroup.printAllEntities();

        Log.print(TAG, "------游戏正式开始运行------");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        this.world.update(delta);

        Camera camera = viewport.getCamera();
        camera.update();
        Batch batch = this.batch;
        batch.setProjectionMatrix(camera.combined);

        //batch.begin();
        //绘制
        this.world.draw(batch);
        //batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin();
        this.world.renderShape(shapeRenderer);
        shapeRenderer.end();
    }

    @Override
    public void resize(int width, int height) {
        this.viewport.setWorldSize(width, height);
        this.cameraController.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        this.world.dispose();
    }
}

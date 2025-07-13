package ttk.muxiuesd.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ttk.muxiuesd.camera.PlayerCamera;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.mod.ModLibManager;
import ttk.muxiuesd.mod.ModLoader;
import ttk.muxiuesd.mod.api.world.ModWorldProvider;
import ttk.muxiuesd.registry.*;
import ttk.muxiuesd.render.RenderProcessorManager;
import ttk.muxiuesd.render.RenderProcessorsReg;
import ttk.muxiuesd.render.instance.EntityGroundRenderProcessor;
import ttk.muxiuesd.render.instance.EntityUndergroundRenderProcessor;
import ttk.muxiuesd.render.instance.ParticleRenderProcessor;
import ttk.muxiuesd.render.instance.WorldChunkRenderProcessor;
import ttk.muxiuesd.shader.ShaderScheduler;
import ttk.muxiuesd.shader.ShadersReg;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.MainWorld;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.wall.WallsReg;

/**
 * 主游戏屏幕
 * */
public class MainGameScreen implements Screen {
    public static String TAG = MainGameScreen.class.getName();

    public Batch batch = new SpriteBatch();
    public Viewport viewport;
    public ShapeRenderer shapeRenderer = new ShapeRenderer() {{
        setAutoShapeType(true);
    }};

    //游戏目前加载的世界，后续可能有多个世界
    private World world;

    @Override
    public void show() {
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        this.viewport = new ScalingViewport(Scaling.fit, w, h, PlayerCamera.INSTANCE.getCamera());
        //this.viewport = new ScalingViewport(Scaling.fit, w, h, cameraController.camera);

        //手动注册游戏内的元素
        Pools.init();
        EventTypes.init();
        Sounds.init();
        Items.init();
        Blocks.init();
        WallsReg.initAllWalls();
        EntityTypes.init();
        Entities.init();

        //初始化着色器调度器
        ShaderScheduler.init();

        this.setWorld(new MainWorld(this));

        RenderProcessorManager.register(RenderProcessorsReg.ENTITY_UNDERGROUND,
            new EntityUndergroundRenderProcessor(PlayerCamera.INSTANCE.getCamera(), ShadersReg.DAYNIGHT_SHADER, 100, this.world));
        RenderProcessorManager.register(RenderProcessorsReg.WORLD_CHUNK,
            new WorldChunkRenderProcessor(PlayerCamera.INSTANCE.getCamera(), ShadersReg.DAYNIGHT_SHADER, 200, this.world));
        RenderProcessorManager.register(RenderProcessorsReg.ENTITY_GROUND,
            new EntityGroundRenderProcessor(PlayerCamera.INSTANCE.getCamera(), ShadersReg.DAYNIGHT_SHADER, 300, this.world));
        RenderProcessorManager.register(RenderProcessorsReg.PARTICLE,
            new ParticleRenderProcessor(PlayerCamera.INSTANCE.getCamera(), ShadersReg.PARTICLE_SHADER, 400, this.world));

        this.world.getSystemManager().initAllSystems();

        //执行mod代码
        ModLibManager.getInstance().loadCoreLib();
        ModLoader.getInstance().loadAllMods();
        ModLoader.getInstance().runAllMods();

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
        batch.begin();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin();

        RenderProcessorManager.render(batch, shapeRenderer);

        shapeRenderer.end();
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        this.viewport.setWorldSize(width, height);
        //this.cameraController.resize(width, height);
        PlayerCamera.INSTANCE.resize(width, height);
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

    /**
     * 设置当前世界
     * */
    private void setWorld(World world) {
        this.world = world;
        //使mod知道当前的游戏世界的实例
        ModWorldProvider.setCurWorld(world);
    }
}

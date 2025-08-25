package ttk.muxiuesd.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.mod.ModLibManager;
import ttk.muxiuesd.mod.ModLoader;
import ttk.muxiuesd.mod.api.world.ModWorldProvider;
import ttk.muxiuesd.registry.*;
import ttk.muxiuesd.render.RenderPipe;
import ttk.muxiuesd.render.RenderProcessorManager;
import ttk.muxiuesd.render.RenderProcessorsReg;
import ttk.muxiuesd.render.camera.GUICamera;
import ttk.muxiuesd.render.camera.PlayerCamera;
import ttk.muxiuesd.render.instance.*;
import ttk.muxiuesd.shader.ShaderScheduler;
import ttk.muxiuesd.shader.ShadersReg;
import ttk.muxiuesd.system.game.GUISystem;
import ttk.muxiuesd.system.manager.GameSystemManager;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.MainWorld;
import ttk.muxiuesd.world.World;

/**
 * 主游戏屏幕
 * */
public class MainGameScreen implements Screen {
    public static String TAG = MainGameScreen.class.getName();

    //游戏目前加载的世界，后续可能有多个世界
    private World world;

    @Override
    public void show() {
        //初始化游戏底层系统
        GameSystemManager.init();

        //手动注册游戏内的元素
        Pools.init();
        EventTypes.init();
        Sounds.init();
        Items.init();
        BlockEntities.init();
        Blocks.init();
        Walls.init();
        EntityTypes.init();
        Entities.init();
        StatusEffects.init();
        WorldInformationType.init();

        //初始化着色器调度器
        ShaderScheduler.init();
        RenderPipe.init();

        this.setWorld(new MainWorld(this));

        RenderProcessorManager.register(RenderProcessorsReg.ENTITY_UNDERGROUND,
            new EntityUndergroundRenderProcessor(
                PlayerCamera.INSTANCE.getCamera(),
                ShadersReg.DAYNIGHT_SHADER,
                100,
                this.world
            )
        );
        RenderProcessorManager.register(RenderProcessorsReg.WORLD_CHUNK,
            new WorldChunkRenderProcessor(
                PlayerCamera.INSTANCE.getCamera(),
                ShadersReg.DAYNIGHT_SHADER,
                200,
                this.world
            )
        );
        RenderProcessorManager.register(RenderProcessorsReg.ENTITY_GROUND,
            new EntityGroundRenderProcessor(
                PlayerCamera.INSTANCE.getCamera(),
                ShadersReg.DAYNIGHT_SHADER,
                300,
                this.world
            )
        );
        RenderProcessorManager.register(RenderProcessorsReg.PARTICLE,
            new ParticleRenderProcessor(
                PlayerCamera.INSTANCE.getCamera(),
                ShadersReg.PARTICLE_SHADER,
                400,
                this.world
            )
        );
        RenderProcessorManager.register(RenderProcessorsReg.GUI,
            new GUIRenderProcessor(
                GUICamera.INSTANCE.getCamera(),
                ShadersReg.DEFAULT_SHADER,
                10000
            )
        );

        //执行mod代码
        ModLibManager.getInstance().loadCoreLib();
        ModLoader.getInstance().loadAllMods();
        ModLoader.getInstance().runAllMods();

        this.world.getSystemManager().initAllSystems();


        GameSystemManager.getInstance().addSystem("GUISystem", new GUISystem());
        GameSystemManager.getInstance().initAllSystems();

        Log.print(TAG, "------游戏正式开始运行------");
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        GameSystemManager.getInstance().update(delta);

        this.world.update(delta);

        /*Camera camera = PlayerCamera.INSTANCE.getCamera();
        camera.update();

        this.batch.setProjectionMatrix(camera.combined);
        this.batch.begin();

        this.shapeRenderer.setProjectionMatrix(camera.combined);
        this.shapeRenderer.begin();

        RenderProcessorManager.render(this.batch, this.shapeRenderer);

        this.shapeRenderer.end();
        this.batch.end();*/
        //处理渲染管线
        RenderPipe.getInstance().handleGameRender();
    }

    @Override
    public void resize(int width, int height) {
        RenderPipe.getInstance().resize(width, height);
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
    public void setWorld(World world) {
        this.world = world;
        //使mod知道当前的游戏世界的实例
        ModWorldProvider.setCurWorld(world);
    }
}

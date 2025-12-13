package ttk.muxiuesd.screen;

import com.badlogic.gdx.Screen;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.mod.ModLibManager;
import ttk.muxiuesd.mod.ModLoader;
import ttk.muxiuesd.mod.api.world.ModWorldProvider;
import ttk.muxiuesd.registry.*;
import ttk.muxiuesd.render.RenderProcessorManager;
import ttk.muxiuesd.render.RenderProcessorsReg;
import ttk.muxiuesd.render.camera.GUICamera;
import ttk.muxiuesd.render.camera.PlayerCamera;
import ttk.muxiuesd.render.instance.*;
import ttk.muxiuesd.render.shader.ShadersReg;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.MainWorld;
import ttk.muxiuesd.world.World;

/**
 * 主游戏屏幕
 */
public class MainGameScreen implements Screen {
    public static String TAG = MainGameScreen.class.getName();

    //游戏目前加载的世界，后续可能有多个世界
    private World world;

    public MainGameScreen () {

    }

    @Override
    public void show() {
        //手动初始化注册游戏世界内的元素
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


        MainWorld mainWorld = new MainWorld(this);
        //游戏世界的渲染处理器注册
        this.initWorldRenderProcessors(mainWorld);
        mainWorld.addAllSystems();
        this.setWorld(mainWorld);

        //执行mod代码
        ModLibManager.getInstance().loadCoreLib();
        ModLoader.getInstance().loadAllMods();
        ModLoader.getInstance().runAllMods();

        //初始化世界系统
        this.getWorld().getSystemManager().initAllSystems();

        Log.print(TAG, "------游戏正式开始运行------");
    }

    private void initWorldRenderProcessors (World world) {
        RenderProcessorManager.register(RenderProcessorsReg.ENTITY_UNDERGROUND,
            new EntityUndergroundRenderProcessor(
                PlayerCamera.INSTANCE.getCamera(),
                ShadersReg.DAYNIGHT_SHADER,
                100,
                world
            )
        );
        RenderProcessorManager.register(RenderProcessorsReg.WORLD_CHUNK,
            new WorldChunkRenderProcessor(
                PlayerCamera.INSTANCE.getCamera(),
                ShadersReg.DAYNIGHT_SHADER,
                200,
                world
            )
        );
        RenderProcessorManager.register(RenderProcessorsReg.ENTITY_GROUND,
            new EntityGroundRenderProcessor(
                PlayerCamera.INSTANCE.getCamera(),
                ShadersReg.DAYNIGHT_SHADER,
                300,
                world
            )
        );
        RenderProcessorManager.register(RenderProcessorsReg.PARTICLE,
            new ParticleRenderProcessor(
                PlayerCamera.INSTANCE.getCamera(),
                ShadersReg.PARTICLE_SHADER,
                400,
                world
            )
        );
        RenderProcessorManager.register(RenderProcessorsReg.GUI,
            new GUIRenderProcessor(
                GUICamera.INSTANCE.getCamera(),
                ShadersReg.DEFAULT_SHADER,
                10000,
                this
            )
        );
    }

    @Override
    public void render(float delta) {
        this.world.update(delta);
        //System.out.println("FPS：" + Gdx.graphics.getFramesPerSecond());
    }

    @Override
    public void resize(int width, int height) {

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

    public World getWorld() {
        return this.world;
    }

    /**
     * 设置当前世界
     */
    public void setWorld(World world) {
        this.world = world;
        //使mod知道当前的游戏世界的实例
        ModWorldProvider.setCurWorld(world);
    }
}

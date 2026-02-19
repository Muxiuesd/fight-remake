package ttk.muxiuesd;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import game.muxiuesd.bedrockcore.app.GameCore;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.lang.FI18N;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.registry.Fonts;
import ttk.muxiuesd.render.RenderPipe;
import ttk.muxiuesd.render.RenderProcessorManager;
import ttk.muxiuesd.render.RenderProcessorsReg;
import ttk.muxiuesd.render.camera.GUICamera;
import ttk.muxiuesd.render.instance.GUIRenderProcessor;
import ttk.muxiuesd.render.shader.ShaderScheduler;
import ttk.muxiuesd.render.shader.ShadersReg;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.screen.StartMenuScreen;
import ttk.muxiuesd.screen.WorldsMenuScreen;
import ttk.muxiuesd.system.game.GUISystem;
import ttk.muxiuesd.system.game.InputHandleSystem;
import ttk.muxiuesd.system.manager.GameSystemManager;
import ttk.muxiuesd.util.Perf;
import ttk.muxiuesd.world.World;

/**
 *  游戏的核心类
 * */
public class FightCore extends GameCore {
    /**
     * 游戏核心，单例模式，全局只有一个实例
     * */
    private static FightCore gameInstance;
    public static FightCore getInstance() {
        if (gameInstance == null) {
            gameInstance = new FightCore();
        }
        return gameInstance;
    }
    private FightCore () {}

    private Screen nextScreen;
    public StartMenuScreen startMenuScreen;
    public WorldsMenuScreen worldsMenuScreen;
    public MainGameScreen mainGameScreen;

    public GUIRenderProcessor guiRenderProcessor;

    @Override
    public void create() {
        this.coreInit();

        this.guiRenderProcessor = new GUIRenderProcessor(
            GUICamera.INSTANCE.getCamera(),
            ShadersReg.DEFAULT_SHADER,
            10000
        );
        //GUI渲染处理器是最先注册的渲染处理器
        RenderProcessorManager.register(RenderProcessorsReg.GUI, this.guiRenderProcessor);

        //游戏界面初始化，在各自的screen里面注册各自需要的渲染处理器
        this.startMenuScreen = new StartMenuScreen();
        this.worldsMenuScreen = new WorldsMenuScreen();
        this.mainGameScreen = new MainGameScreen();

        GameSystemManager.getInstance().addSystem("InputHandleSystem", InputHandleSystem.getInstance());
        GameSystemManager.getInstance().addSystem("GUISystem", GUISystem.getInstance());
        //初始化游戏底层系统
        GameSystemManager.getInstance().initAllSystems();

        //setScreen(this.mainGameScreen);
        setScreen(this.startMenuScreen);
    }

    public void coreInit () {
        /// 以下是整个游戏核心加载初始化的东西
        Fonts.init();
        FI18N.init();
        EventTypes.init();
        RegistrantGroup.init();

        //初始化着色器调度器
        ShaderScheduler.init();
        RenderPipe.init();
        //初始化底层游戏系统
        GameSystemManager.init();
    }

    @Override
    public void render () {
        //延迟交换游戏屏幕
        exchangeScreen();

        float deltaTime = Gdx.graphics.getDeltaTime();

        Perf.begin();
        //游戏系统的更新
        Perf.start("game_system_update");
        GameSystemManager.getInstance().update(deltaTime);
        Perf.stop();

        //screen的更新
        Perf.start("game_screen_update");
        updateScreen(deltaTime);
        Perf.stop();

        //游戏渲染部分
        //处理渲染管线
        Perf.start("game_render");
        RenderPipe.getInstance().handleGameRender();
        Perf.stop();

        Perf.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            System.out.println("------");
            Perf.RECORDER.getDataStack().forEach((data) -> {
                System.out.println("操作：" + data.getName() + " 耗时：" + data.getCostTime() + " 毫秒");

            });
            System.out.println("------");
        }
    }

    @Override
    public void resize (int width, int height) {
        //通知渲染管线resize
        RenderPipe.getInstance().resize(width, height);
        super.resize(width, height);
    }

    @Override
    public void dispose() {
        super.dispose();
        GameSystemManager.getInstance().dispose();
        getActiveScreen().dispose();
    }

    /**
     * 获取当前的游戏世界
     * */
    public World getWorld () {
        if (this.mainGameScreen != null && this.worldIsRunning()) return this.mainGameScreen.getWorld();
        return null;
    }

    /**
     * 检测游戏世界是否运行
     * */
    public boolean worldIsRunning () {
        return getActiveScreen() == this.mainGameScreen;
    }
}

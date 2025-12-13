package ttk.muxiuesd;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.lang.FI18N;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.registry.Fonts;
import ttk.muxiuesd.render.RenderPipe;
import ttk.muxiuesd.render.shader.ShaderScheduler;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.screen.StartMenuScreen;
import ttk.muxiuesd.system.game.GUISystem;
import ttk.muxiuesd.system.game.InputHandleSystem;
import ttk.muxiuesd.system.manager.GameSystemManager;

/**
 *  游戏核心启动入口
 * */
public class FightGameMain extends Game {
    private StartMenuScreen startMenuScreen;
    private MainGameScreen mainGameScreen;

    @Override
    public void create() {
        //先行加载
        Fonts.init();
        FI18N.init();

        EventTypes.init();
        RegistrantGroup.init();

        //初始化着色器调度器
        ShaderScheduler.init();
        RenderPipe.init();
        //添加底层游戏系统
        GameSystemManager.init();

        //游戏界面初始化，在各自的screen里面注册各自需要的渲染处理器
        this.startMenuScreen = new StartMenuScreen();
        this.mainGameScreen = new MainGameScreen();

        GameSystemManager.getInstance().addSystem("InputHandleSystem", InputHandleSystem.getInstance());
        GameSystemManager.getInstance().addSystem("GUISystem", GUISystem.getInstance());
        //初始化游戏底层系统
        GameSystemManager.getInstance().initAllSystems();

        setScreen(this.mainGameScreen);
    }

    @Override
    public void render () {
        //游戏系统的更新
        float deltaTime = Gdx.graphics.getDeltaTime();
        GameSystemManager.getInstance().update(deltaTime);

        //screen的更新
        if (getScreen() != null) getScreen().render(deltaTime);

        //游戏渲染部分
        ScreenUtils.clear(Color.BLACK);
        //处理渲染管线
        RenderPipe.getInstance().handleGameRender();
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
        getScreen().dispose();
    }
}

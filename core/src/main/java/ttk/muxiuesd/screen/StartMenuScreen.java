package ttk.muxiuesd.screen;

import com.badlogic.gdx.Screen;
import ttk.muxiuesd.system.game.GUISystem;
import ttk.muxiuesd.ui.screen.StartMenuUIScreen;

/**
 * 游戏的开始菜单界面
 * */
public class StartMenuScreen implements Screen {

    private StartMenuUIScreen uiScreen;

    public StartMenuScreen () {
        //注册主菜单界面的GUI渲染处理器
        /*RenderProcessorManager.register(RenderProcessorsReg.START_MENU_GUI,
            new MenuGUIRenderProcessor(
                GUICamera.INSTANCE.getCamera(),
                ShadersReg.DEFAULT_SHADER,
                9999
            )
        );*/

        this.uiScreen = new StartMenuUIScreen();
    }

    @Override
    public void show () {
        //设置UI界面
        GUISystem.getInstance().setCurScreen(this.getUiScreen());
    }

    @Override
    public void render (float delta) {

    }

    @Override
    public void resize (int width, int height) {

    }

    @Override
    public void pause () {

    }

    @Override
    public void resume () {

    }

    @Override
    public void hide () {
        GUISystem.getInstance().setCurScreen(null);
    }

    @Override
    public void dispose () {

    }

    public StartMenuUIScreen getUiScreen () {
        return uiScreen;
    }

    public StartMenuScreen setUiScreen (StartMenuUIScreen uiScreen) {
        this.uiScreen = uiScreen;
        return this;
    }
}

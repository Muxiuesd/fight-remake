package ttk.muxiuesd.screen;

import com.badlogic.gdx.Screen;
import ttk.muxiuesd.system.game.GUISystem;
import ttk.muxiuesd.ui.screen.WorldsMenuUIScreen;

/**
 * 游戏世界（存档）选择界面
 * */
public class WorldsMenuScreen implements Screen {
    WorldsMenuUIScreen menuUIScreen;

    public WorldsMenuScreen () {
        this.menuUIScreen = new WorldsMenuUIScreen();
    }

    @Override
    public void show () {
        //设置UI界面
        GUISystem.getInstance().setCurScreen(this.getMenuUIScreen());
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

    public WorldsMenuUIScreen getMenuUIScreen () {
        return menuUIScreen;
    }

    public WorldsMenuScreen setMenuUIScreen (WorldsMenuUIScreen menuUIScreen) {
        this.menuUIScreen = menuUIScreen;
        return this;
    }
}

package ttk.muxiuesd.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import ttk.muxiuesd.FightCore;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.system.game.GUISystem;
import ttk.muxiuesd.ui.screen.WorldsMenuUIScreen;

/**
 * 游戏世界（存档）选择界面
 * */
public class WorldsMenuScreen implements Screen {
    public static final Color BACKGROUND_COLOR = new Color(0.1f, 0.2f, 0.5f, 1f);
    private WorldsMenuUIScreen menuUIScreen;

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
        ScreenUtils.clear(BACKGROUND_COLOR);

        //按下退出按键就退回到主菜单界面
        if (KeyBindings.Exit.wasJustPressed()) {
            FightCore.getInstance().changeScreen(FightCore.getInstance().startMenuScreen);
        }
    }

    /**
     * 读取存档目录
     * */
    public void readSaves () {

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
        return this.menuUIScreen;
    }

    public WorldsMenuScreen setMenuUIScreen (WorldsMenuUIScreen menuUIScreen) {
        this.menuUIScreen = menuUIScreen;
        return this;
    }
}

package ttk.muxiuesd.screen;

import com.badlogic.gdx.Screen;
import ttk.muxiuesd.render.RenderProcessorManager;
import ttk.muxiuesd.render.RenderProcessorsReg;
import ttk.muxiuesd.render.camera.GUICamera;
import ttk.muxiuesd.render.instance.MenuGUIRenderProcessor;
import ttk.muxiuesd.render.shader.ShadersReg;

/**
 * 游戏的开始菜单界面
 * */
public class StartMenuScreen implements Screen {

    @Override
    public void show () {
        RenderProcessorManager.register(RenderProcessorsReg.START_MENU_GUI,
            new MenuGUIRenderProcessor(
                GUICamera.INSTANCE.getCamera(),
                ShadersReg.DEFAULT_SHADER,
                9999
            )
        );
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

    }

    @Override
    public void dispose () {

    }
}

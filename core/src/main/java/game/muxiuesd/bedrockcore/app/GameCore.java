package game.muxiuesd.bedrockcore.app;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

/**
 * 游戏核心
 * */
public abstract class GameCore implements ApplicationListener {
    private Screen activeScreen;    //当前活跃的游戏屏幕
    private Screen nextScreen;      //下一个要被切换的游戏屏幕

    @Override
    public void render () {
        //延迟交换游戏屏幕
        if (this.nextScreen != null) {
            if (this.activeScreen != null) {
                this.activeScreen.hide();
            }
            this.activeScreen = this.nextScreen;
            this.activeScreen.show();
            this.activeScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            this.nextScreen = null;
        }

        if (this.activeScreen != null) this.activeScreen.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void pause () {
        if (this.activeScreen != null) this.activeScreen.pause();
    }

    @Override
    public void resume () {
        if (this.activeScreen != null) this.activeScreen.resume();
    }

    @Override
    public void resize (int width, int height) {
        if (this.activeScreen != null) this.activeScreen.resize(width, height);
    }

    @Override
    public void dispose () {
        if (this.activeScreen != null) this.activeScreen.hide();
    }

    /**
     * 设置下一个屏幕
     * */
    public void setScreen (Screen screen) {
        this.nextScreen = screen;
    }

    /**
     * 获取当前活跃的游戏屏幕
     * */
    public Screen getActiveScreen () {
        return this.activeScreen;
    }
}

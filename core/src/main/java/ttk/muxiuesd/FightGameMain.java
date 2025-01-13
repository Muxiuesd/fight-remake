package ttk.muxiuesd;

import com.badlogic.gdx.Game;
import ttk.muxiuesd.screen.MainGameScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class FightGameMain extends Game {
    @Override
    public void create() {
        setScreen(new MainGameScreen());
    }

    @Override
    public void dispose() {
        super.dispose();
        getScreen().dispose();
    }
}

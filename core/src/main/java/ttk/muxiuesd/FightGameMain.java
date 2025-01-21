package ttk.muxiuesd;

import com.badlogic.gdx.Game;
import ttk.muxiuesd.screen.MainGameScreen;

/**
 *
 * */
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

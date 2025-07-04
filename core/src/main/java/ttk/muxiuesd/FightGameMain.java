package ttk.muxiuesd;

import com.badlogic.gdx.Game;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.screen.MainGameScreen;

/**
 *游戏界面
 * */
public class FightGameMain extends Game {
    @Override
    public void create() {
        //先行加载
        //EventBus eventBus = EventBus.getInstance();
        EventTypes.init();
        RegistrantGroup.init();

        setScreen(new MainGameScreen());
    }

    @Override
    public void dispose() {
        super.dispose();
        getScreen().dispose();
    }
}

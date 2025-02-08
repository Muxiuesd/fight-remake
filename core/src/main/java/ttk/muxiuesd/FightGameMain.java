package ttk.muxiuesd;

import com.badlogic.gdx.Game;
import ttk.muxiuesd.mod.ModLoader;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.world.event.EventBus;

/**
 *游戏界面
 * */
public class FightGameMain extends Game {
    @Override
    public void create() {
        //先行加载
        EventBus eventBus = EventBus.getInstance();
        RegistrantGroup registrantGroup = new RegistrantGroup();
        ModLoader.getInstance().loadAllMods();

        setScreen(new MainGameScreen());
    }

    @Override
    public void dispose() {
        super.dispose();
        getScreen().dispose();
    }
}

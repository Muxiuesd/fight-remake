package ttk.muxiuesd;

import com.badlogic.gdx.Game;
import ttk.muxiuesd.mod.ModLoader;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.screen.MainGameScreen;
import ttk.muxiuesd.world.block.Blocks;
import ttk.muxiuesd.world.event.EventBus;

/**
 *游戏界面
 * */
public class FightGameMain extends Game {
    @Override
    public void create() {
        EventBus eventBus = EventBus.getInstance();
        RegistrantGroup registrantGroup = new RegistrantGroup();
        ModLoader.getInstance().loadAllMods();
        Blocks.printAllBlock();
        setScreen(new MainGameScreen());
    }

    @Override
    public void dispose() {
        super.dispose();
        getScreen().dispose();
    }
}

package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.math.GridPoint2;
import game.muxiuesd.bedrockcore.app.ui.components.UIButton;
import game.muxiuesd.bedrockcore.app.ui.components.UIButtonListItem;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.FightCore;
import ttk.muxiuesd.ui.text.Text;

/**
 * 世界存档按钮，点击进入存档
 * */
public class WorldSaveButtonUI extends UIButtonListItem implements UIButton.ClickEvent {
    private String worldName;
    private long worldSeed;

    public WorldSaveButtonUI (String worldName, long worldSeed) {
        super(Text.ofText(worldName), VOID_CLICK_EVENT);
        this.worldName = worldName;
        this.worldSeed = worldSeed;

        setClickEvent(this);
    }

    @Override
    public boolean handle (UIButton button, GridPoint2 interactPos) {
        //游戏世界名称设置到全局变量值上
        Fight.WORLD_NAME.setValue(this.worldName);
        Fight.WORLD_SEED.setValue(this.worldSeed);

        //切换到游戏世界屏幕
        FightCore.getInstance().setScreen(FightCore.getInstance().mainGameScreen);

        return false;
    }

}

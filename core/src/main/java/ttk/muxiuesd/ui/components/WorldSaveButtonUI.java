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

    public WorldSaveButtonUI (String worldName) {
        super(Text.ofText(worldName), VOID_CLICK_EVENT);
        this.worldName = worldName;

        setClickEvent(this);
    }

    @Override
    public boolean handle (UIButton button, GridPoint2 interactPos) {
        //游戏世界名称设置到全局变量值上
        Fight.WORLD_NAME.setValue(this.worldName);

        //切换到游戏世界屏幕
        FightCore.getInstance().setScreen(FightCore.getInstance().mainGameScreen);

        return false;
    }
}

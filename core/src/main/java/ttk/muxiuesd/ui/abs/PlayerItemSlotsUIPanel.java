package ttk.muxiuesd.ui.abs;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.GridPoint2;
import game.muxiuesd.bedrockcore.app.ui.components.UIPanel;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.components.MouseSlotUI;

/**
 * 含有玩家物品的slotUI的面板，抽象类
 * */
public abstract class PlayerItemSlotsUIPanel extends UIPanel {
    private PlayerSystem playerSystem;

    public PlayerItemSlotsUIPanel (PlayerSystem playerSystem) {
        super();
        this.playerSystem = playerSystem;
    }
    public PlayerItemSlotsUIPanel (PlayerSystem playerSystem,
                                   float x, float y,
                                   float width, float height,
                                   GridPoint2 interactGridSize) {
        super(x, y, width, height, interactGridSize);
        this.playerSystem = playerSystem;
    }

    public PlayerSystem getPlayerSystem () {
        return this.playerSystem;
    }

    public PlayerItemSlotsUIPanel setPlayerSystem (PlayerSystem playerSystem) {
        this.playerSystem = playerSystem;
        return this;
    }

    @Override
    protected void onClickBlank (GridPoint2 panelPos, int button) {
        if (button == Input.Buttons.RIGHT) {
            //右键：从鼠标槽位丢出1个物品
            MouseSlotUI.dropOneIfActiveOn(this.getScreen(), this.getPlayerSystem().getPlayer());
        }else {
            //左键：鼠标槽位的物品全部丢出
            MouseSlotUI.dropItemIfActiveOn(this.getScreen(), this.getPlayerSystem().getPlayer());
        }
    }
}

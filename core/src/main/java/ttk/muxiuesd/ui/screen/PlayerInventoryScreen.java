package ttk.muxiuesd.ui.screen;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.PlayerInventoryUIPanel;
import ttk.muxiuesd.ui.abs.UIScreen;
import ttk.muxiuesd.util.Util;

/**
 * 玩家背包界面屏幕
 * <p>
 * 由背景面板、各种槽位组成
 * */
public class PlayerInventoryScreen extends UIScreen {
    private static PlayerInventoryUIPanel INVENTORY_UI_PANEL_INSTANCE;
    public static PlayerInventoryUIPanel getInventoryUIPanel() {
        return INVENTORY_UI_PANEL_INSTANCE;
    }
    public static void setInventoryUIPanel(PlayerInventoryUIPanel inventoryUIPanel) {
        if (inventoryUIPanel != null) INVENTORY_UI_PANEL_INSTANCE = inventoryUIPanel;
    }

    private PlayerSystem playerSystem;

    public PlayerInventoryScreen(PlayerSystem playerSystem) {
        this.playerSystem = playerSystem;

        TextureRegion background = Util.loadTextureRegion(
            Fight.getId("player_inventory"),
            Fight.UITexturePath("inventory.png")
        );

        setInventoryUIPanel(new PlayerInventoryUIPanel(
            playerSystem, background,
            background.getRegionWidth(), background.getRegionHeight()
        ));

        addComponent(INVENTORY_UI_PANEL_INSTANCE);
    }


}

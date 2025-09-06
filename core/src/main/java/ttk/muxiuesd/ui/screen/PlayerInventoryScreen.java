package ttk.muxiuesd.ui.screen;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.PlayerInventoryUIPanel;
import ttk.muxiuesd.ui.abs.UIScreen;
import ttk.muxiuesd.util.Util;

/**
 * 玩家背包界面屏幕
 * */
public class PlayerInventoryScreen extends UIScreen {
    private PlayerSystem playerSystem;

    public PlayerInventoryScreen(PlayerSystem playerSystem) {
        this.playerSystem = playerSystem;

        TextureRegion background = Util.loadTextureRegion(
            Fight.getId("player_inventory"),
            Fight.UITexturePath("inventory.png")
        );
        PlayerInventoryUIPanel inventoryUIPanel = new PlayerInventoryUIPanel(
            playerSystem, background,
            background.getRegionWidth(), background.getRegionHeight()
        );

        addComponent(inventoryUIPanel);
    }
}

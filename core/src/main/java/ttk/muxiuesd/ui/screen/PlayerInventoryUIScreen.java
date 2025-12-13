package ttk.muxiuesd.ui.screen;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.PlayerEffectUIPanel;
import ttk.muxiuesd.ui.PlayerInventoryUIPanel;
import ttk.muxiuesd.ui.abs.UIScreen;
import ttk.muxiuesd.ui.components.MouseSlotUI;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 玩家背包界面屏幕
 * <p>
 * 由背景面板、各种槽位组成
 * */
public class PlayerInventoryUIScreen extends UIScreen {
    private static PlayerInventoryUIPanel INVENTORY_UI_PANEL_INSTANCE;

    public static PlayerInventoryUIPanel getInventoryUIPanel() {
        return INVENTORY_UI_PANEL_INSTANCE;
    }

    public static void setInventoryUIPanel(PlayerInventoryUIPanel inventoryUIPanel) {
        if (inventoryUIPanel != null) INVENTORY_UI_PANEL_INSTANCE = inventoryUIPanel;
    }

    private PlayerSystem playerSystem;
    private PlayerEffectUIPanel effectUIPanel;


    public PlayerInventoryUIScreen (PlayerSystem playerSystem) {
        this.playerSystem = playerSystem;

        TextureRegion background = Util.loadTextureRegion(
            Fight.ID("player_inventory"),
            Fight.UITexturePath("inventory.png")
        );

        setInventoryUIPanel(new PlayerInventoryUIPanel(
            playerSystem, background,
            background.getRegionWidth(), background.getRegionHeight()
        ));

        addComponent(INVENTORY_UI_PANEL_INSTANCE);
        addComponent(this.effectUIPanel = new PlayerEffectUIPanel(
            INVENTORY_UI_PANEL_INSTANCE.getX() + INVENTORY_UI_PANEL_INSTANCE.getWidth(),
            INVENTORY_UI_PANEL_INSTANCE.getY() + INVENTORY_UI_PANEL_INSTANCE.getHeight(),
            0, 0,
            new GridPoint2(1, 1),
            this.playerSystem
        ));
    }

    @Override
    public void hide () {
        //如果鼠标上还有物品的时候关闭玩家背包界面，就自动把鼠标上的物品丢出来
        MouseSlotUI mouseSlotUI = MouseSlotUI.getInstance();
        if (getInventoryUIPanel().hasComponent(mouseSlotUI) && !mouseSlotUI.isNullSlot()) {
            ItemStack itemStack = mouseSlotUI.getItemStack();
            mouseSlotUI.clearItem();
            this.playerSystem.getPlayer().dropItem(itemStack);
        }
    }
}

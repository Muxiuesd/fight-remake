package ttk.muxiuesd.ui.screen;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import game.muxiuesd.bedrockcore.app.ui.components.UIPanel;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.ui.PlayerCreateTabUIPanel;
import ttk.muxiuesd.ui.PlayerEffectUIPanel;
import ttk.muxiuesd.ui.PlayerInventoryUIPanel;
import ttk.muxiuesd.ui.abs.FightUIScreen;
import ttk.muxiuesd.ui.components.MouseSlotUI;
import ttk.muxiuesd.ui.components.TooltipUI;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 玩家相关的界面屏幕，持有各种玩家相关的UI面板
 * <p>
 * 由背景面板、各种槽位组成
 * */
public class PlayerUIScreen extends FightUIScreen {
    private static PlayerInventoryUIPanel INVENTORY_UI_PANEL_INSTANCE;
    private static PlayerCreateTabUIPanel CREATE_TAB_UI_PANEL_INSTANCE;

    public static PlayerInventoryUIPanel getInventoryUIPanel() {
        return INVENTORY_UI_PANEL_INSTANCE;
    }

    public static void setInventoryUIPanel(PlayerInventoryUIPanel inventoryUIPanel) {
        if (inventoryUIPanel != null) INVENTORY_UI_PANEL_INSTANCE = inventoryUIPanel;
    }

    public static PlayerCreateTabUIPanel getCreateTabUIPanel() {
        return CREATE_TAB_UI_PANEL_INSTANCE;
    }

    public static void setCreateTabUIPanel(PlayerCreateTabUIPanel createTabUIPanel) {
        if (createTabUIPanel != null) CREATE_TAB_UI_PANEL_INSTANCE = createTabUIPanel;
    }

    private PlayerSystem playerSystem;
    private PlayerEffectUIPanel effectUIPanel;  //状态UI面板

    private UIPanel currentTopPanel;   //当前顶层显示的面板


    public PlayerUIScreen (PlayerSystem playerSystem) {
        this.playerSystem = playerSystem;

        /// 玩家背包
        TextureRegion playerInventoryBackground = Util.loadTextureRegion(
            Fight.ID("player_inventory"),
            Fight.UITexturePath("inventory.png")
        );
        setInventoryUIPanel(new PlayerInventoryUIPanel(
            playerSystem, playerInventoryBackground,
            playerInventoryBackground.getRegionWidth(), playerInventoryBackground.getRegionHeight()
            )
        );

        /// 玩家创造背包
        setCreateTabUIPanel(new PlayerCreateTabUIPanel(playerSystem));

        //默认先显示玩家背包面板
        this.setCurrentTopPanel(getInventoryUIPanel());

        addComponent(this.effectUIPanel = new PlayerEffectUIPanel(
            this.currentTopPanel.getX() + this.currentTopPanel.getWidth(),
            this.currentTopPanel.getY() + this.currentTopPanel.getHeight(),
            0, 0,
            new GridPoint2(1, 1),
            this.playerSystem
        ));

    }

    @Override
    public void hide () {
        //如果鼠标上还有物品的时候关闭玩家背包界面，就自动把鼠标上的物品丢出来
        MouseSlotUI mouseSlotUI = MouseSlotUI.getInstance();
        if (this.currentTopPanel.hasComponent(mouseSlotUI) && !mouseSlotUI.isNullSlot()) {
            ItemStack itemStack = mouseSlotUI.getItemStack();
            mouseSlotUI.clearItem();
            this.playerSystem.getPlayer().dropItem(itemStack);
        }

        //用一下父类的调用
        super.hide();
    }

    @Override
    public void update (float delta) {
        //切换面板
        if (KeyBindings.PlayerBackpackScreenChange.wasJustPressed()) {
            if (this.currentTopPanel == getInventoryUIPanel()) {
                this.setCurrentTopPanel(getCreateTabUIPanel());
            }
            else if (this.currentTopPanel == getCreateTabUIPanel()) {
                this.setCurrentTopPanel(getInventoryUIPanel());
            }
        }

        super.update(delta);

        //状态UI面板位置一直在当前显示的顶层面板的最右边贴着
        if (this.effectUIPanel != null && this.currentTopPanel != null) {
            this.effectUIPanel.setPosition(
                this.currentTopPanel.getX() + this.currentTopPanel.getWidth() + 1f,
                this.currentTopPanel.getY() + this.currentTopPanel.getHeight()
            );
        }
    }

    /**
     * 设置当前面板上需要顶层显示的面板
     * */
    public void setCurrentTopPanel (UIPanel panel) {
        if (this.currentTopPanel != null) {
            removeComponent(this.currentTopPanel);
        }

        this.currentTopPanel = panel;
        addComponent(this.currentTopPanel);

        TooltipUI.deactivate();
    }
}

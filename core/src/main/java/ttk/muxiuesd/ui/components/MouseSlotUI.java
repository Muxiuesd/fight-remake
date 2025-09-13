package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.ui.screen.PlayerInventoryScreen;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 鼠标物品槽UI
 * */
public class MouseSlotUI extends SlotUI {
    //单例模式
    public static final MouseSlotUI INSTANCE = new MouseSlotUI();

    private ItemStack itemStack;

    private MouseSlotUI() {
        super(10000f, 10000f, SLOT_WIDTH, SLOT_HEIGHT, new GridPoint2(16, 16));
        setEnabled(false);
    }

    /**
     * 激活
     * */
    public static MouseSlotUI activate () {
        if (PlayerInventoryScreen.inventoryUIPanel != null) {
            INSTANCE.setPosition(Util.getMouseUIPosition());
            PlayerInventoryScreen.inventoryUIPanel.addComponent(INSTANCE);
        }
        return INSTANCE;
    }

    public static void deactivate () {
        if (PlayerInventoryScreen.inventoryUIPanel != null) {
            PlayerInventoryScreen.inventoryUIPanel.removeComponent(INSTANCE);
        }
    }

    @Override
    public boolean isNullSlot () {
        return this.getItemStack() == null;
    }

    @Override
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public void setItemStack (ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void clearItem () {
        this.itemStack = null;
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        if (this.isNullSlot()) return;

        Vector2 mouseUIPosition = Util.getMouseUIPosition();
        Item item = this.getItemStack().getItem();
        batch.draw(item.texture,
            mouseUIPosition.x - getWidth() / 2,
            mouseUIPosition.y - getHeight() / 2,
            getWidth(), getHeight());
    }
}

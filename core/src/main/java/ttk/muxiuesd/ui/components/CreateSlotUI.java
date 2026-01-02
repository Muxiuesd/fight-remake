package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.world.item.ItemGroup;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 创造物品槽位UI
 * */
public class CreateSlotUI extends SlotUI {
    private ItemGroup itemGroup;
    private int index = 0;

    public CreateSlotUI (float x, float y) {
        super(x, y, SlotUI.SLOT_WIDTH, SlotUI.SLOT_HEIGHT);
    }

    /**
     * 点击创造物品槽位获取物品
     * */
    @Override
    public boolean click (GridPoint2 interactPos) {
        if (!isNullSlot()) {
            //如果是有物品的槽位
            if (MouseSlotUI.getInstance().isNullSlot()) {
                ItemStack stack = this.getItemStack();
                ItemStack copy = stack.copy(KeyBindings.PlayerShift.wasPressed() ? stack.getItem().getProperty().getMaxCount() : 1);
                MouseSlotUI.activate(getScreen()).setItemStack(copy);
            }
        }else {
            //如果是没有物品的槽位
            if (MouseSlotUI.getInstance().isActive()
                && MouseSlotUI.getInstance().getScreen() == getScreen()
                && ! MouseSlotUI.getInstance().isNullSlot()) {
                //如果此时鼠标物品槽位是激活状态、并且和此UI组件在同一个UI屏幕上并且有物品就失活鼠标物品槽位UI顺带清除鼠标物品槽位
                MouseSlotUI.deactivate().clearItem();
            }
        }
        return false;
    }

    @Override
    public boolean isNullSlot () {
        return this.getIndex() >= this.getItemGroup().getItemsList().size();
    }

    @Override
    public ItemStack getItemStack () {
        if (this.isNullSlot()) return null;
        return this.getItemGroup().get(this.getIndex());
    }

    public ItemGroup getItemGroup () {
        return this.itemGroup;
    }

    public CreateSlotUI setItemGroup (ItemGroup itemGroup) {
        this.itemGroup = itemGroup;
        return this;
    }

    public int getIndex () {
        return this.index;
    }

    public CreateSlotUI setIndex (int index) {
        if (index >= 0) this.index = index;
        return this;
    }
}

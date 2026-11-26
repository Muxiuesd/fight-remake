package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import game.muxiuesd.bedrockcore.app.ui.components.UIPanel;
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
    public boolean click (GridPoint2 interactPos, int button) {
        //只有左键点击获取物品，右键由子类自行判断
        if (button != Input.Buttons.LEFT) return super.click(interactPos, button);
        if (!isNullSlot()) {
            //如果是有物品的槽位
            if (MouseSlotUI.getInstance().isNullSlot()) {
                ItemStack stack = this.getItemStack();
                ItemStack copy = stack.copy(KeyBindings.PlayerShift.wasPressed() ? stack.getItem().getProperty().getMaxCount() : 1);
                MouseSlotUI.activate(getScreen()).setItemStack(copy);
            }
        }else {
            //如果是没有物品的槽位（空白格子）
            if (MouseSlotUI.getInstance().isActive()
                && MouseSlotUI.getInstance().getScreen() == getScreen()
                && ! MouseSlotUI.getInstance().isNullSlot()) {
                //【故意设计】在创造背包中点击空白格子，可直接销毁当前鼠标上拿着的真实物品。
                //用于"快速丢弃/清理物品"：从真实快捷栏拿起物品 → 随手点创造页空白格 → 物品即被删除，不会再放回。
                //此行为是刻意的快捷销毁设计，而非缺陷（当初 H-U4 误判为空槽误删，经确认属于设计意图，予以保留）。
                //注意：只有鼠标槽位与当前 UI 同屏且拖拽着真实物品时才触发，不会误删非鼠标持有的物品。
                MouseSlotUI.deactivate().clearItem();
            }
        }
        return false;
    }

    /**
     *  不绘制数量字体
     * */
    @Override
    public void drawAmount(Batch batch, UIPanel parent, float renderX, float renderY, int amount) {
        //super.drawAmount(batch, parent, renderX, renderY, amount);
    }

    @Override
    public boolean isNullSlot () {
        return this.getIndex() >= this.getItemGroup().getItemsList().size();
    }

    @Override
    public ItemStack getItemStack () {
        if (this.isNullSlot()) return ItemStack.VOID;
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

package ttk.muxiuesd.ui.components;

import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.equipment.EquipmentItem;

/**
 * 装备物品槽位的UI组件
 * */
public class EquipmentPlayerSlotUI extends PlayerSlotUI {
    public final EquipmentItem.Type type;

    public EquipmentPlayerSlotUI (PlayerSystem playerSystem, int index, float x, float y) {
        super(playerSystem, index, x, y);
        this.type = EquipmentItem.Type.values()[index];
    }

    /**
     * 对应的装备类型才能放进对应的装备槽位
     * */
    @Override
    public boolean checkItemType (ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof EquipmentItem equipmentItem) {
            return equipmentItem.equipmentType == type;
        }

        return false;
    }

    /**
     * 获取装备背包容器
     * */
    @Override
    public Inventory getInventory () {
        return getPlayerSystem().getPlayer().getEquipmentBackpack();
    }
}

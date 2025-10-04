package ttk.muxiuesd.ui.components;

import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.system.PlayerSystem;

/**
 * 装备物品槽位的UI组件
 * */
public class EquipmentSlotUI extends SlotUI {
    public EquipmentSlotUI (PlayerSystem playerSystem, int index, float x, float y) {
        super(playerSystem, index, x, y);
    }

    /**
     * 获取装备背包容器
     * */
    @Override
    public Inventory getInventory () {
        return getPlayerSystem().getPlayer().getEquipmentBackpack();
    }
}

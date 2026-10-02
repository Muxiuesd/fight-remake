package ttk.muxiuesd.ui.components;

import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.equipment.EquipmentItem;
import ttk.muxiuesd.world.item.equipment.EquipmentType;

/**
 * 装备物品槽位的UI组件
 * <p>
 * 每个槽位持有一种已注册的 {@link EquipmentType} 实例：
 * 只有当"物品持有的装备类型"与"槽位持有的装备类型"是同一实例时，物品才能放入。
 * <p>
 * {@code index} 是与装备背包格一一对应的下标，其值与槽位的显示坐标一样，
 * 由写 UI 的 {@code PlayerInventoryUIPanel} 在构造槽位时决定。
 * */
public class EquipmentPlayerSlotUI extends PlayerSlotUI {
    /// 本槽位接受的装备类型（注册实例）
    public final EquipmentType type;

    public EquipmentPlayerSlotUI (PlayerSystem playerSystem, int index, EquipmentType type, float x, float y) {
        super(playerSystem, index, x, y);
        this.type = type;
    }

    /**
     * 对应的装备类型才能放进对应的装备槽位
     * */
    @Override
    public boolean checkItemType (ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item instanceof EquipmentItem equipmentItem) {
            //注册实例的引用同一性：同一种装备类型在游戏中只有唯一实例
            return equipmentItem.getEquipmentType() == type;
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

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
    /// 槽位索引到装备类型的显式映射（不依赖 values() 顺序，避免枚举扩容时越界/错位）
    private static final EquipmentItem.Type[] SLOT_TYPE_MAP = {
        EquipmentItem.Type.HELMET,
        EquipmentItem.Type.CHESTPLATE,
        EquipmentItem.Type.LEGGINGS,
        EquipmentItem.Type.BOOTS
    };

    public final EquipmentItem.Type type;

    public EquipmentPlayerSlotUI (PlayerSystem playerSystem, int index, float x, float y) {
        super(playerSystem, index, x, y);
        //索引超出映射范围时用 OTHERS 兜底，避免 ArrayIndexOutOfBounds
        this.type = index >= 0 && index < SLOT_TYPE_MAP.length
            ? SLOT_TYPE_MAP[index]
            : EquipmentItem.Type.OTHERS;
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

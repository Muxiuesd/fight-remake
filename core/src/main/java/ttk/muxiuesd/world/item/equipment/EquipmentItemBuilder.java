package ttk.muxiuesd.world.item.equipment;

import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.builder.ItemBuilder;

/**
 * 装备物品构建器
 * <p>
 * 替代原先每个装备各一个空壳子类（EquipmentDiamond*）的做法，
 * 装备的类型通过 {@link EquipmentItem.Type} 指定。
 */
public class EquipmentItemBuilder implements ItemBuilder<EquipmentItem> {
    private final EquipmentItem.Type type;

    private EquipmentItemBuilder (EquipmentItem.Type type) {
        this.type = type;
    }

    public static EquipmentItemBuilder of (EquipmentItem.Type type) {
        return new EquipmentItemBuilder(type);
    }

    @Override
    public EquipmentItem build () {
        return new EquipmentItem(this.type, EquipmentItem.createDefaultProperty());
    }
}
package ttk.muxiuesd.registry.itemgroups;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Items;
import ttk.muxiuesd.world.item.ItemGroup;

/**
 * 装备物品组
 * */
public class EquipmentItemGroup {
    public static final ItemGroup GROUP = new ItemGroup.Builder()
        .setIdentifier(Fight.ID("equipment_item_group"))
        .setIconItemStack(Items.DIAMOND_HELMET)
        .build()
        .selfAction(group -> {
        group
            .add(Items.DIAMOND_HELMET)
            .add(Items.DIAMOND_CHESTPLATE)
            .add(Items.DIAMOND_LEGGINGS)
            .add(Items.DIAMOND_BOOTS)
        ;
    });
}

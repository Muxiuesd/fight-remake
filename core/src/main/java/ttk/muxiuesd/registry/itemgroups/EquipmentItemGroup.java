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
            .add(Items.IRON_HELMET)
            .add(Items.IRON_CHESTPLATE)
            .add(Items.IRON_LEGGINGS)
            .add(Items.IRON_BOOTS)
            .add(Items.GOLD_HELMET)
            .add(Items.GOLD_CHESTPLATE)
            .add(Items.GOLD_LEGGINGS)
            .add(Items.GOLD_BOOTS)
            .add(Items.DIAMOND_HELMET)
            .add(Items.DIAMOND_CHESTPLATE)
            .add(Items.DIAMOND_LEGGINGS)
            .add(Items.DIAMOND_BOOTS)
        ;
    });
}

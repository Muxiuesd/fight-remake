package ttk.muxiuesd.registry.itemgroups;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Items;
import ttk.muxiuesd.world.item.ItemGroup;

/**
 * 武器物品组
 * */
public class WeaponItemGroup {
    public static final ItemGroup GROUP = new ItemGroup.Builder()
        .setIdentifier(Fight.ID("weapon_item_group"))
        .setIconItemStack(Items.IRON_SWORD)
        .build()
        .selfAction(group -> {
        group
            .add(Items.IRON_SWORD)
            .add(Items.TEST_WEAPON)
            ;
    });
}

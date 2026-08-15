package ttk.muxiuesd.registry.itemgroups;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Items;
import ttk.muxiuesd.world.item.ItemGroup;

/**
 * 材料物品组
 * */
public class MaterialItemGroup {
    public static final ItemGroup GROUP = new ItemGroup.Builder()
        .setIdentifier(Fight.ID("material_item_group"))
        .setIconItemStack(Items.STICK)
        .build()
        .selfAction(group -> {
        group
            .add(Items.STICK)
            .add(Items.SLIME_BALL)
            .add(Items.COAL)
            .add(Items.IRON_INGOT)
            .add(Items.GOLD_INGOT)
            .add(Items.SPAWN_EGG_SLIME)
            .add(Items.SPAWN_EGG_PUFFER_FISH)
            ;
    });
}

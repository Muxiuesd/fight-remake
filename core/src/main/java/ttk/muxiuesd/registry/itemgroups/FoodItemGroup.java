package ttk.muxiuesd.registry.itemgroups;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Items;
import ttk.muxiuesd.world.item.ItemGroup;

/**
 * 食物物品组
 * */
public class FoodItemGroup {
    public static final ItemGroup GROUP = new ItemGroup.Builder()
        .setIdentifier(Fight.ID("food_item_group"))
        .setIconItemStack(Items.FISH)
        .build()
        .selfAction(group -> {
        group
            .add(Items.FISH)
            .add(Items.PUFFER_FISH)
            .add(Items.POTATO)
            .add(Items.POTATO_BAKED)
            .add(Items.POTION_HEAL_LEVEL_1)
            .add(Items.POTION_HEAL_LEVEL_2)
            .add(Items.POTION_HEAL_LEVEL_3)
        ;
    });
}

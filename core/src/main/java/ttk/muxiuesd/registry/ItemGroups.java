package ttk.muxiuesd.registry;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.itemgroups.*;
import ttk.muxiuesd.world.item.ItemGroup;

/**
 * 游戏内所有的物品组的注册
 * */
public final class ItemGroups {
    public static void init () {}

    public static final ItemGroup MATERIAL_ITEM = register(MaterialItemGroup.GROUP);
    public static final ItemGroup FOOD_ITEM = register(FoodItemGroup.GROUP);
    public static final ItemGroup WEAPON_ITEM = register(WeaponItemGroup.GROUP);
    public static final ItemGroup EQUIPMENT_ITEM = register(EquipmentItemGroup.GROUP);

    public static final ItemGroup NATURE_BLOCK_ITEM = register(BlockItemGroups.NATURE_GROUP);
    public static final ItemGroup COLOR_BLOCK_ITEM = register(BlockItemGroups.COLOR_GROUP);
    public static final ItemGroup TOOL_BLOCK_ITEM = register(BlockItemGroups.TOOL_GROUP);


    public static final ItemGroup COMMON_ITEM = register("common_item").selfAction((self) -> {
        self
            .add(Items.SLIME_BALL)
            .add(Items.COAL)
            .add(Items.STICK)
            .add(Items.PUFFER_FISH)

            .add(Items.DIAMOND_HELMET)
            .add(Items.DIAMOND_CHESTPLATE)
            .add(Items.DIAMOND_LEGGINGS)
            .add(Items.DIAMOND_BOOTS)

            .add(Items.SPAWN_EGG_SLIME)
            .add(Items.SPAWN_EGG_PUFFER_FISH)

            .add(Items.FURNACE)
            .add(Items.CRAFTING_TABLE)

            .add(Items.WOOL_BLACK)
            .add(Items.WOOL_BLUE)
            .add(Items.WOOL_BROWN)
            .add(Items.WOOL_CYAN)
            .add(Items.WOOL_GRAY)
            .add(Items.WOOL_GREEN)
            .add(Items.WOOL_LIGHT_BLUE)
            .add(Items.WOOL_LIME)
            .add(Items.WOOL_MAGENTA)
            .add(Items.WOOL_ORANGE)
            .add(Items.WOOL_PINK)
            .add(Items.WOOL_PURPLE)
            .add(Items.WOOL_RED)
            .add(Items.WOOL_SILVER)
            .add(Items.WOOL_WHITE)
            .add(Items.WOOL_YELLOW)

            .add(Items.GRASS)
            .add(Items.FARMLAND_DRY)
            .add(Items.STONE)

            .add(Items.POTATO)
        ;
        });


    public static ItemGroup register (String name) {
        return register(new ItemGroup(Identifier.of(Fight.ID(name))));
    }

    public static ItemGroup register (ItemGroup group) {
        if (group.getIdentifier() == null) {
            Log.error(ItemGroups.class.getName(), "物品组的ID标识符不能为null！！！", new NullPointerException());
        }
        return Registries.ITEM_GROUP.register(group.getIdentifier(), group);
    }
}

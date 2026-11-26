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

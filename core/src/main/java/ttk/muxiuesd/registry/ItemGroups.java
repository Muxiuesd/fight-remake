package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.item.ItemGroup;

/**
 * 游戏内所有的物品组的注册
 * */
public final class ItemGroups {
    public static void init () {}

    public static final ItemGroup COMMON_ITEM = register("common_item")
        .add(Items.SLIME_BALL)
        .add(Items.COAL)
        .add(Items.STICK)
        .add(Items.PUFFER_FISH)
        .add(Items.DIAMOND_HELMET)
        .add(Items.DIAMOND_CHESTPLATE)
        .add(Items.DIAMOND_LEGGINGS)
        .add(Items.DIAMOND_BOOTS)
        .add(Items.FURNACE);



    public static ItemGroup register (String name) {
        return register(new ItemGroup(Fight.ID(name)));
    }
    public static ItemGroup register (ItemGroup group) {
        return Registries.ITEM_GROUP.register(new Identifier(group.getGroupID()), group);
    }
}

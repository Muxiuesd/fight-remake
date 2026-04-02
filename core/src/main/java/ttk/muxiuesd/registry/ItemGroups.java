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
            .add(Items.FURNACE)
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
            .add(Items.SPAWN_EGG_SLIME)
        ;
        });




    public static ItemGroup register (String name) {
        return register(new ItemGroup(Fight.ID(name)));
    }
    public static ItemGroup register (ItemGroup group) {
        return Registries.ITEM_GROUP.register(new Identifier(group.getGroupID()), group);
    }
}

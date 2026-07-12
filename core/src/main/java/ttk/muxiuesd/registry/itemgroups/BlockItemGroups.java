package ttk.muxiuesd.registry.itemgroups;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Items;
import ttk.muxiuesd.world.item.ItemGroup;

/**
 * 方块物品组
 * */
public class BlockItemGroups {
    public static final ItemGroup NATURE_GROUP = new ItemGroup.Builder()
        .setIdentifier(Fight.ID("nature_block_item_group"))
        .setIconItemStack(Items.GRASS)
        .build()
        .selfAction((group) -> {
            group
                .add(Items.GRASS)
                .add(Items.FARMLAND_DRY)
                .add(Items.STONE)
                .add(Items.SAND)
            ;
        });

    public static final ItemGroup COLOR_GROUP = new ItemGroup.Builder()
        .setIdentifier(Fight.ID("color_block_item_group"))
        .setIconItemStack(Items.WOOL_GREEN)
        .build()
        .selfAction((group) -> {
            group
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
                .add(Items.WOOL_YELLOW);
        });

    public static final ItemGroup TOOL_GROUP = new ItemGroup.Builder()
        .setIdentifier(Fight.ID("tool_block_item_group"))
        .setIconItemStack(Items.TEST_BLOCK)
        .build()
        .selfAction((group) -> {
            group
                .add(Items.FURNACE)
                .add(Items.CRAFTING_TABLE)
                ;
        });
}

package ttk.muxiuesd.world.entity.player;

import ttk.muxiuesd.registry.Items;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 玩家的测试
 * */
public class PlayerDebugger {
    /**
     * 给玩家放一些物品
     * */
    public static void items (Player player) {
        if (player.getBackpack().isEmpty()) {
            player.addItemsToBackpack(
                new ItemStack(Items.WOOD_SWORD),
                new ItemStack(Items.STONE_SWORD),
                new ItemStack(Items.IRON_SWORD),
                new ItemStack(Items.GOLD_SWORD),
                new ItemStack(Items.TEST_WEAPON),
                new ItemStack(Items.STICK),
                new ItemStack(Items.FURNACE),
                new ItemStack(Items.CRAFTING_TABLE),
                new ItemStack(Items.FISH_POLE),
                new ItemStack(Items.SMOOTH_STONE),
                new ItemStack(Items.TORCH),
                new ItemStack(Items.IRON_SWORD),
                new ItemStack(Items.TEST_WEAPON),
                new ItemStack(Items.STICK),
                new ItemStack(Items.FURNACE),
                new ItemStack(Items.CRAFTING_TABLE),
                new ItemStack(Items.FISH_POLE),
                new ItemStack(Items.DIAMOND_HELMET),
                new ItemStack(Items.DIAMOND_CHESTPLATE),
                new ItemStack(Items.DIAMOND_LEGGINGS),
                new ItemStack(Items.DIAMOND_BOOTS),
                new ItemStack(Items.TORCH)
            );
        }
    }
}

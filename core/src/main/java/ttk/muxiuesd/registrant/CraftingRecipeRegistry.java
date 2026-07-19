package ttk.muxiuesd.registrant;

import ttk.muxiuesd.recipe.CraftingTableRecipe;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 工作台合成配方注册表
 * */
public class CraftingRecipeRegistry extends Registries.DefaultRegistry<CraftingTableRecipe> {

    /**
     * 根据输入的物品查找配方表
     * */
    public CraftingTableRecipe findRecipe (ItemStack[] inputs) {
        if (inputs == null || inputs.length != 9) return null;
        //TODO 实现更高效的查找方式
        for (CraftingTableRecipe recipe : getMap().values()) {
            if (recipe.matches(inputs)) {
                return recipe;
            }
        }
        return null;
    }
}

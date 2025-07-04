package ttk.muxiuesd.recipe;

import ttk.muxiuesd.interfaces.RecipeOutput;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 烧炼配方
 * */
public abstract class CookingRecipe implements RecipeOutput {
    //这个配方的id
    private String id;
    //需要的物品输入，用于匹配
    private ItemStack input;

    public CookingRecipe (String id, ItemStack input) {
        this.id = id;
        this.input = input;
    }

    /**
     * 输入是否匹配
     * */
    public boolean match (ItemStack inputStack) {
        return input.equals(inputStack);
    }

    public String getId () {
        return id;
    }

    public CookingRecipe setId (String id) {
        this.id = id;
        return this;
    }

    public ItemStack getInput () {
        return input;
    }

    public CookingRecipe setInput (ItemStack input) {
        this.input = input;
        return this;
    }
}

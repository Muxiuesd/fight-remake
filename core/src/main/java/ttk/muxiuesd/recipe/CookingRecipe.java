package ttk.muxiuesd.recipe;

import ttk.muxiuesd.interfaces.RecipeOutput;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 烧炼配方
 * */
public abstract class CookingRecipe implements RecipeOutput {
    private String id;
    private ItemStack input;

    public CookingRecipe (String id, ItemStack input) {
        this.id = id;
        this.input = input;
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

package ttk.muxiuesd.recipe;

import ttk.muxiuesd.world.item.ItemStack;

/**
 * 烧炼配方
 * */
public class CookingRecipe {
    private String id;
    private ItemStack input;
    private ItemStack output;

    public CookingRecipe () {
    }
    public CookingRecipe (String id, ItemStack input, ItemStack output) {
        this.id = id;
        this.input = input;
        this.output = output;
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

    public ItemStack getOutput () {
        return output;
    }

    public CookingRecipe setOutput (ItemStack output) {
        this.output = output;
        return this;
    }
}

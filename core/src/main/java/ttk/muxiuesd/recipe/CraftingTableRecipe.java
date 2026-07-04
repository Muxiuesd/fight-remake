package ttk.muxiuesd.recipe;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 工作台配方
 * */
public class CraftingTableRecipe {
    private Identifier identifier;
    private ItemStack[] inputs;
    private ItemStack output;






    public Identifier getIdentifier () {
        return this.identifier;
    }

    public CraftingTableRecipe setIdentifier (Identifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public ItemStack[] getInputs () {
        return this.inputs;
    }

    public CraftingTableRecipe setInputs (ItemStack[] inputs) {
        this.inputs = inputs;
        return this;
    }

    public ItemStack getOutput () {
        return this.output;
    }

    public CraftingTableRecipe setOutput (ItemStack output) {
        this.output = output;
        return this;
    }
}

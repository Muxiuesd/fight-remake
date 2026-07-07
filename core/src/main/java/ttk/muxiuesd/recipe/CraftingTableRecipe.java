package ttk.muxiuesd.recipe;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 工作台配方
 * */
public abstract class CraftingTableRecipe {
    private Identifier identifier;
    private ItemStack[] inputs;
    private ItemStack output;

    public CraftingTableRecipe(Identifier identifier, ItemStack[] inputs ,ItemStack output) {
        this.identifier = identifier;
        this.inputs = inputs;
        this.output = output;
    }

    /**
     * 抽象方法：配方适配
     * */
    public abstract boolean matches (ItemStack[] inputs);


    public Identifier getIdentifier () {
        return this.identifier;
    }

    public CraftingTableRecipe setIdentifier (Identifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public ItemStack[] getInputs() {
        return this.inputs;
    }

    public void setInputs(ItemStack[] inputs) {
        this.inputs = inputs;
    }

    public ItemStack getOutput () {
        return this.output;
    }

    public CraftingTableRecipe setOutput (ItemStack output) {
        this.output = output;
        return this;
    }
}

package ttk.muxiuesd.recipe;

import ttk.muxiuesd.world.item.ItemStack;

/**
 * 工作台配方
 * */
public abstract class CraftingTableRecipe {
    private ItemStack[] inputs;
    private ItemStack output;

    public CraftingTableRecipe(ItemStack[] inputs ,ItemStack output) {
        this.inputs = inputs;
        this.output = output;
    }

    /**
     * 抽象方法：配方适配
     * */
    public abstract boolean matches (ItemStack[] inputs);

    /**
     * 获取配对输入的物品堆叠（原始数据），对于这个的直接修改相当于修改配方（非必要不修改）
     * */
    public ItemStack[] getInputs() {
        return this.inputs;
    }

    public void setInputs(ItemStack[] inputs) {
        this.inputs = inputs;
    }

    /**
     * 获取输出的物品堆叠（原始数据）。如果是要应用输出物品，记得 {@link ItemStack#copy()} 防止污染原始数据
     * */
    public ItemStack getOutput () {
        return this.output;
    }

    public CraftingTableRecipe setOutput (ItemStack output) {
        this.output = output;
        return this;
    }
}

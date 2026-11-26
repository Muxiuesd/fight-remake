package ttk.muxiuesd.recipe;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.RecipeOutput;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 烧炼配方
 * */
public abstract class CookingRecipe implements RecipeOutput {
    //这个配方的id
    private Identifier identifier;
    //需要的物品输入，用于匹配
    private ItemStack input;

    public CookingRecipe (Identifier identifier, ItemStack input) {
        this.identifier = identifier;
        this.input = input;
    }

    /**
     * 输入是否匹配
     * */
    public boolean match (ItemStack inputStack) {
        return input.equals(inputStack);
    }

    public String getId () {
        return this.getIdentifier() == null ? null : this.getIdentifier().getID();
    }

    public Identifier getIdentifier () {
        return this.identifier;
    }

    public CookingRecipe setIdentifier (Identifier identifier) {
        //Identifier 只在注册阶段给定，注册过后不允许修改
        if (this.identifier != null && !this.identifier.equals(identifier)) {
            throw new IllegalStateException("Identifier 已设置，禁止修改！烧炼配方：" + this.identifier.getID() + " -> " + identifier.getID());
        }
        this.identifier = identifier;
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

package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 实体所拥有的物品背包
 * */
public class Backpack {
    private Array<ItemStack> itemStacks;

    public Backpack (int size) {
        this.itemStacks = new Array<>(size);

    }

    public ItemStack getItemStack (int index) {
        return itemStacks.get(index);
    }

    public void setItemStack (int index, ItemStack itemStack) {
        //this.itemStacks.add(itemStack);
        this.itemStacks.insert(index, itemStack);
    }

    public ItemStack clear (int index) {
        if (itemStacks.get(index) != null) return this.itemStacks.removeIndex(index);
        return null;
    }
}

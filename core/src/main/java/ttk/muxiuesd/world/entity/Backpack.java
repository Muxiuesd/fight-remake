package ttk.muxiuesd.world.entity;

import ttk.muxiuesd.world.item.ItemStack;

import java.util.HashMap;

/**
 * 实体所拥有的物品背包
 * */
public class Backpack {
    private HashMap<Integer, ItemStack> itemStacks;
    private final int maxCapacity;

    public Backpack (int size) {
        this.itemStacks = new HashMap<>(size);
        this.maxCapacity = size;
    }

    public ItemStack getItemStack (int index) {
        if (this.exceed(index)) throw new IndexOutOfBoundsException();
        return itemStacks.get(index);
    }

    public void setItemStack (int index, ItemStack itemStack) {
        if (this.exceed(index)) throw new IndexOutOfBoundsException();
        this.itemStacks.put(index, itemStack);
    }

    /**
     * 丢出物品
     * @return 若有东西可丢则是被丢出来的物品堆叠，诺没东西可丢则是null
     * */
    public ItemStack dropItem (int index, int amount) {
        ItemStack itemStack = this.getItemStack(index);
        if (itemStack == null) return null;
        if (amount <= 0) return null;

        //全部数量丢出，若传的数量大于则算全部丢出
        if (amount >= itemStack.getAmount()) {
            return this.clear(index);
            //return itemStack;
        }
        //非全部数量丢出
        itemStack.setAmount(itemStack.getAmount() - amount);
        return new ItemStack(itemStack.getItem(), amount);
    }


    /**
     * 清除某一个位置的物品堆叠
     * @return 若被清除的位置有物品，则返回被清除的物品；若没有则返回null
     * */
    public ItemStack clear (int index) {
        if (this.exceed(index)) throw new IndexOutOfBoundsException();
        if (this.itemStacks.get(index) != null) return this.itemStacks.remove(index);
        return null;
    }

    /**
     * 是否超出背包大小
     * */
    public boolean exceed (int index) {
        return index >= this.maxCapacity || index < 0;
    }

    /**
     * 获取背包大小，并非获取背包里面装了多少
     * */
    public int getSize () {
        return this.maxCapacity;
    }
}

package ttk.muxiuesd.world.item;

import ttk.muxiuesd.world.item.abs.Item;

/**
 * 物品堆栈
 * */
public class ItemStack {
    private final Item item;
    private int amount;

    public ItemStack(Item item) {
        //默认直接最大堆叠值
        this(item, item.property.getMaxCount());
    }
    public ItemStack (Item item, int amount) {
        this.item = item;
        this.amount = amount;
    }


    public Item getItem () {
        return this.item;
    }

    public int getAmount () {
        return amount;
    }

    public void setAmount (int amount) {
        if (amount > 0) this.amount = amount;
    }
}

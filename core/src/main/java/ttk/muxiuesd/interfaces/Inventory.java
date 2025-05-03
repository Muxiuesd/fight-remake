package ttk.muxiuesd.interfaces;

import ttk.muxiuesd.world.item.ItemStack;

/**
 * 容器接口
 * */
public interface Inventory {

    /**
     * 丢出物品
     * */
    ItemStack dropItem (int index, int amount);

    /**
     * 添加物品
     * <p>
     * @return 返回被捡起来后的物品堆叠，若全部被捡起来则返回null
     * */
    ItemStack addItem (ItemStack itemStack);

    /**
     * 清除物品
     * */
    ItemStack clear (int index);

    /**
     * 获取物品堆叠
     * */
    ItemStack getItemStack (int index);

    /**
     * 设置物品堆叠
     * */
    void setItemStack (int index, ItemStack item);

    /**
     * 获取背包的大小（最多能装多少ItemStack）
     * */
    int getSize ();

    /**
     * 获取目前装的ItemStack的数量
     * */
    int getCapacity ();

    default boolean isEmpty () {
        return this.getCapacity() == 0;
    }

    default boolean isFull () {
        return this.getCapacity() == this.getSize();
    }

    /**
     * 是否超出背包大小
     * */
    default boolean exceed (int index) {
        return index >= this.getSize() || index < 0;
    }

}

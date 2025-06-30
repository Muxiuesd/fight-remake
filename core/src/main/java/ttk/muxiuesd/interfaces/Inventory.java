package ttk.muxiuesd.interfaces;

import ttk.muxiuesd.world.item.ItemStack;

import java.util.Objects;

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
     * @return 返回被添加后的物品堆叠，若全部被添加则返回null
     * */
    ItemStack addItem (ItemStack itemStack);

    /**
     * 清除物品
     * */
    ItemStack clear (int index);

    /**
     * 清除数量为零的物品堆叠
     * */
    default void clear () {
        for (int i = 0;i < this.getSize();i++){
            ItemStack itemStack = this.getItemStack(i);
            if (itemStack != null && itemStack.getAmount() == 0){
                clear(i);
            }
        }
    }

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

    /**
     * 容器对于一个物品堆叠来说是否算被装满了
     * */
    default boolean isFull (ItemStack itemStack) {
        for (int index = 0; index < this.getSize(); index++) {
            ItemStack stack = this.getItemStack(index);
            //还有空位说明没装满
            if (stack == null) return false;
            if (Objects.equals(itemStack.getItem().getID(), stack.getItem().getID())
                && stack.getAmount() < stack.getProperty().getMaxCount()) {
                //如果有相同的物品堆叠并且容器里的物品堆叠数量并没有达到最大值，也不算满
                return false;
            }
        }
        return this.getCapacity() == this.getSize();
    }

    /**
     * 是否超出背包大小
     * */
    default boolean exceed (int index) {
        return index >= this.getSize() || index < 0;
    }

}

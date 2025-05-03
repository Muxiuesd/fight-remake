package ttk.muxiuesd.world.entity;

import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * 实体所拥有的物品背包
 * */
public class Backpack implements Inventory, Updateable {
    private final LinkedHashMap<Integer, ItemStack> itemStacks;
    private final int size;

    public Backpack (int size) {
        this.itemStacks = new LinkedHashMap<>(size);
        this.size = size;
    }

    @Override
    public ItemStack getItemStack (int index) {
        if (this.exceed(index)) throw new IndexOutOfBoundsException();
        return this.itemStacks.get(index);
    }

    @Override
    public void setItemStack (int index, ItemStack itemStack) {
        if (this.exceed(index)) throw new IndexOutOfBoundsException();
        this.itemStacks.put(index, itemStack);
    }

    /**
     * 丢出物品
     * @return 若有东西可丢则是被丢出来的物品堆叠，诺没东西可丢则是null
     * */
    @Override
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
        //System.out.println("还剩：" + itemStack.getAmount() + " 个");
        return new ItemStack(itemStack.getItem(), amount);
    }

    @Override
    public ItemStack addItem (ItemStack itemStack) {
        if (this.isFull(itemStack)) return itemStack;

        //TODO 解决相同物品不能存放多个堆叠的bug
        boolean[] nullStacks = new boolean[this.size];
        for (int i = 0; i < this.size; ++i) {
            ItemStack stack = this.itemStacks.get(i);
            if (stack != null) {
                //同类型物品合并，（目前只检测Id是否相同）
                if (Objects.equals(stack.getItem().getID(), itemStack.getItem().getID())) {
                    //堆叠数量达到上限直接跳过
                    if (stack.getAmount() >= stack.getProperty().getMaxCount()) continue;

                    int newAmount = stack.getAmount() + itemStack.getAmount();
                    int maxCount = stack.getItem().property.getMaxCount();
                    if (newAmount <= maxCount) {
                        stack.setAmount(newAmount);
                        return null;
                    } else {
                        stack.setAmount(maxCount);
                        itemStack.setAmount(newAmount - maxCount);
                        return itemStack;
                    }
                }
                //不同类型就跳过
                nullStacks[i] = false;
                continue;
            }
            nullStacks[i] = true;
        }
        //查找空位，然后把物品堆叠放进去
        for (int i = 0; i < nullStacks.length; ++i) {
            if (nullStacks[i]) {
                this.setItemStack(i, itemStack);
                return null;
            }
        }
        return itemStack;
    }

    /**
     * 捡起物品
     * <p>
     * @return 返回被捡起来后的物品堆叠，若全部被捡起来则返回null
     * */
    public ItemStack pickUpItem (ItemStack itemStack) {
        return this.addItem(itemStack);
    }


    /**
     * 清除某一个位置的物品堆叠
     * @return 若被清除的位置有物品，则返回被清除的物品；若没有则返回null
     * */
    @Override
    public ItemStack clear (int index) {
        if (this.exceed(index)) throw new IndexOutOfBoundsException();
        if (this.itemStacks.get(index) != null) return this.itemStacks.remove(index);
        return null;
    }

    /**/
    public ItemStack clear (ItemStack itemStack) {
        if (this.itemStacks.containsValue(itemStack)) {
            this.itemStacks.values().remove(itemStack);
            return itemStack;
        }
        return null;
    }

    /**
     * 是否超出背包大小
     * */
    @Override
    public boolean exceed (int index) {
        return index >= this.getSize() || index < 0;
    }

    @Override
    public int getSize () {
        return this.size;
    }

    @Override
    public int getCapacity () {
        return this.itemStacks.size();
    }

    @Override
    public void update (float delta) {
        this.itemStacks.forEach((index, itemStack) -> {
           itemStack.update(delta);
        });
    }
}

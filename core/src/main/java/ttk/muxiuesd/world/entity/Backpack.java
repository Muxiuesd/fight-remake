package ttk.muxiuesd.world.entity;

import game.muxiuesd.bedrockcore.app.interfaces.Updateable;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.LinkedHashMap;

/**
 * 实体所拥有的物品背包
 * */
public class Backpack implements Inventory, Updateable {
    public static final int DEFAULT_SIZE = 1;
    public static final Codec<Inventory> CODEC = new InventoryCodec<>(Backpack::new);

    private LinkedHashMap<Integer, ItemStack> itemStacks;
    private int size;

    public Backpack () {
        //默认设置大小为1
        this(DEFAULT_SIZE);
    }
    public Backpack (int size) {
        setSize(size);
    }

    @Override
    public ItemStack getItemStack (int index) {
        if (this.exceed(index)) throw new IndexOutOfBoundsException();
        ItemStack itemStack = this.itemStacks.get(index);
        //空槽统一返回空物品堆叠（内部存储仍为 null，VOID 不可被存储/序列化/更新）
        return itemStack == null ? ItemStack.VOID : itemStack;
    }

    @Override
    public void setItemStack (int index, ItemStack itemStack) {
        if (this.exceed(index)) throw new IndexOutOfBoundsException();
        //空堆叠等价清空（内部存 null，避免 VOID 被 update 调用 getItem() 而 NPE）
        this.itemStacks.put(index, itemStack == null || itemStack == ItemStack.VOID ? null : itemStack);
    }

    /**
     * 设置大小
     * */
    @Override
    public void setSize (int size) {
        if (size < 1) {
            Log.error(this.getClass().getName(), "新设置的背包大小不合法，新值：" + size + "！！！", new IllegalArgumentException());
            return;
        }
        this.size = size;
        //只迁移合法索引内的非空堆叠（缩小时丢弃越界残留键，新 map 不保留空槽键）
        LinkedHashMap<Integer, ItemStack> newMap = new LinkedHashMap<>(size);
        if (this.itemStacks != null) {
            for (int i = 0; i < size; i++) {
                ItemStack stack = this.itemStacks.get(i);
                if (stack != null) newMap.put(i, stack);
            }
        }
        this.itemStacks = newMap;
    }

    /**
     * 清除某一个位置的物品堆叠
     * @return 若被清除的位置有物品，则返回被清除的物品；若没有则返回空物品堆叠
     * */
    @Override
    public ItemStack clear (int index) {
        if (this.exceed(index)) throw new IndexOutOfBoundsException();
        if (this.itemStacks.get(index) != null) return this.itemStacks.remove(index);
        return ItemStack.VOID;
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
        //只统计非空堆叠（map 中可能存在值为 null 的空槽键，size() 会误算）
        int count = 0;
        for (ItemStack stack : this.itemStacks.values()) {
            if (stack != null) count++;
        }
        return count;
    }

    @Override
    public void update (float delta) {
        //更新堆叠并清理空槽键（setItemStack(VOID) 会留下值为 null 的键，反复放取会累积）
        //注意：接口 default update 无法用 super 调用（父类是 Object），这里直接实现
        this.itemStacks.entrySet().removeIf(entry -> {
            ItemStack itemStack = entry.getValue();
            if (itemStack != null) {
                itemStack.update(delta);
                return false;
            }
            return true;
        });
    }
}

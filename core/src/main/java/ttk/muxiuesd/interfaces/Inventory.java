package ttk.muxiuesd.interfaces;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.DataResult;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 容器接口
 * <p>
 * 实现类只需提供存储相关方法（{@link #getItemStack}/{@link #setItemStack}/
 * {@link #setSize}/{@link #getSize}/{@link #getCapacity}/{@link #clear(int)}），
 * 即获得完整的容器行为（添加/丢弃/拾取/清理/更新）
 * */
public interface Inventory {

    /**
     * 丢出物品
     * @return 若有东西可丢则是被丢出来的物品堆叠，没有东西可丢则是空物品堆叠
     * */
    default ItemStack dropItem (int index, int amount) {
        ItemStack itemStack = this.getItemStack(index);
        if (itemStack.isVoid()) return ItemStack.VOID;
        if (amount <= 0) return ItemStack.VOID;

        //全部数量丢出，若传的数量大于则算全部丢出
        if (amount >= itemStack.getAmount()) {
            return this.clear(index);
        }
        //非全部数量丢出
        itemStack.setAmount(itemStack.getAmount() - amount);
        return new ItemStack(itemStack.getItem(), amount);
    }

    /**
     * 添加物品
     * @return 返回被添加后的物品堆叠，若全部被添加则返回空物品堆叠
     * */
    default ItemStack addItem (ItemStack itemStack) {
        if (this.isFull(itemStack)) return itemStack;

        //尝试合并到已有的堆叠（物品与属性全等才合并）
        for (int i = 0; i < this.getSize(); ++i) {
            ItemStack stack = this.getItemStack(i);
            if (!stack.isVoid() && !stack.isFull() && stack.equals(itemStack)) {
                int newAmount = stack.getAmount() + itemStack.getAmount();
                int maxCount = stack.getItem().getProperty().getMaxCount();
                if (newAmount <= maxCount) {
                    stack.setAmount(newAmount);
                    return ItemStack.VOID;
                } else {
                    //合并到上限后剩余部分继续与其他堆叠合并或放入空位
                    stack.setAmount(maxCount);
                    itemStack.setAmount(newAmount - maxCount);
                }
            }
        }

        //查找空位，然后把物品堆叠放进去
        for (int i = 0; i < this.getSize(); ++i) {
            if (this.getItemStack(i).isVoid()) {
                this.setItemStack(i, itemStack);
                return ItemStack.VOID;
            }
        }

        return itemStack;
    }

    /**
     * 捡起物品
     * @return 返回被捡起来后的物品堆叠，若全部被捡起来则返回空物品堆叠
     * */
    default ItemStack pickUpItem (ItemStack itemStack) {
        return this.addItem(itemStack);
    }

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
            if (!itemStack.isVoid() && itemStack.getAmount() == 0){
                clear(i);
            }
        }
    }

    /**
     * 清除指定的物品堆叠（引用匹配，只清除第一个持有该实例的槽位）
     * @return 若被清除的槽位有物品，则返回被清除的物品；没有则返回空物品堆叠
     * */
    default ItemStack clear (ItemStack itemStack) {
        for (int i = 0; i < this.getSize(); i++) {
            if (this.getItemStack(i) == itemStack) {
                return this.clear(i);
            }
        }
        return ItemStack.VOID;
    }

    /**
     * 更新容器内所有物品堆叠
     * */
    default void update (float delta) {
        for (int i = 0; i < this.getSize(); i++) {
            ItemStack itemStack = this.getItemStack(i);
            if (!itemStack.isVoid()) {
                itemStack.update(delta);
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
     * 设置容器的大小（最多能装多少ItemStack）
     * */
    void setSize (int size);

    /**
     * 获取容器的大小（最多能装多少ItemStack）
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
            if (stack.isVoid()) return false;
            if (stack.getItem() == itemStack.getItem()
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


    /**
     * 容器接口的编解码器实现
     * */
    class InventoryCodec<T extends Inventory> extends Codec<Inventory>{
        /**
         * 创建一个容器的编解码器
         * */
        public static <T extends Inventory> Codec<Inventory> create (Supplier<T>  supplier) {
            return new InventoryCodec<>(supplier);
        }

        public final Supplier<T> supplier;

        public InventoryCodec (Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        public RawObject encode(Inventory backpack) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("size", Codec.INT.encode(backpack.getSize()).unwrap());

            Map<String, Object> items = new LinkedHashMap<>();
            for (int i = 0; i < backpack.getSize(); i++) {
                ItemStack stack = backpack.getItemStack(i);
                //空堆叠不序列化（VOID 的 item 为 null，编码会 NPE）
                if (!stack.isVoid()) {
                    items.put(String.valueOf(i), ItemStack.CODEC.encode(stack).unwrap());
                }
            }
            map.put("items", RawObject.ofMap(items).unwrap());
            return RawObject.ofMap(map);
        }

        @Override
        public DataResult<Inventory> decode(RawObject input) {
            if (!input.isMap()) return DataResult.error("Expected a map");

            Map<String, Object> rawMap = input.asMap().get();
            Object sizeRaw = rawMap.get("size");
            if (sizeRaw == null) return DataResult.error("Missing size");

            DataResult<Integer> sizeResult = Codec.INT.decode(Codec.wrap(sizeRaw));
            if (!sizeResult.isSuccess()) return DataResult.error("Invalid size: " + sizeResult.error().orElse(""));

            int size = sizeResult.result().get();

            Inventory inventoryInstance = this.supplier.get();
            inventoryInstance.setSize(size);

            Object itemsRaw = rawMap.get("items");
            if (itemsRaw != null) {
                RawObject itemsObj = Codec.wrap(itemsRaw);
                if (itemsObj.isMap()) {
                    Map<String, Object> itemsMap = itemsObj.asMap().get();
                    for (Map.Entry<String, Object> entry : itemsMap.entrySet()) {
                        try {
                            int index = Integer.parseInt(entry.getKey());
                            DataResult<ItemStack> stackResult = ItemStack.CODEC.decode(Codec.wrap(entry.getValue()));
                            if (stackResult.isSuccess()) {
                                inventoryInstance.setItemStack(index, stackResult.result().get());
                            }
                        } catch (NumberFormatException e) {
                            // 跳过无效键
                        } catch (Exception e) {
                            // 未知物品 id 等解码异常（旧存档数据）：跳过该槽位，不中断整个背包解码
                        }
                    }
                }
            }
            return DataResult.success(inventoryInstance);
        }
    }

}

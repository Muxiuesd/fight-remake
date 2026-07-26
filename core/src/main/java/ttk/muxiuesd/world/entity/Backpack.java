package ttk.muxiuesd.world.entity;

import game.muxiuesd.bedrockcore.app.interfaces.Updateable;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.DataResult;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 实体所拥有的物品背包
 * */
public class Backpack implements Inventory, Updateable {
    public static final Codec<Backpack> CODEC = new Codec<>() {
        @Override
        public RawObject encode(Backpack backpack) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("size", Codec.INT.encode(backpack.getSize()).unwrap());

            Map<String, Object> items = new LinkedHashMap<>();
            for (int i = 0; i < backpack.getSize(); i++) {
                ItemStack stack = backpack.getItemStack(i);
                if (stack != null) {
                    items.put(String.valueOf(i), ItemStack.CODEC.encode(stack).unwrap());
                }
            }
            map.put("items", RawObject.ofMap(items).unwrap());
            return RawObject.ofMap(map);
        }

        @Override
        public DataResult<Backpack> decode(RawObject input) {
            if (!input.isMap()) return DataResult.error("Expected a map");
            Map<String, Object> rawMap = input.asMap().get();
            Object sizeRaw = rawMap.get("size");
            if (sizeRaw == null) return DataResult.error("Missing size");
            DataResult<Integer> sizeResult = Codec.INT.decode(Codec.wrap(sizeRaw));
            if (!sizeResult.isSuccess()) return DataResult.error("Invalid size: " + sizeResult.error().orElse(""));
            int size = sizeResult.result().get();
            Backpack backpack = new Backpack(size);
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
                                backpack.setItemStack(index, stackResult.result().get());
                            }
                        } catch (NumberFormatException e) {
                            // 跳过无效键
                        }
                    }
                }
            }
            return DataResult.success(backpack);
        }
    };


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

        // 尝试合并到已有的堆叠
        for (int i = 0; i < this.size; ++i) {
            ItemStack stack = this.itemStacks.get(i);
            /*if (stack != null && Objects.equals(stack.getItem().getID(), itemStack.getItem().getID())) {*/
            if(stack != null && !stack.isFull() && stack.equals(itemStack)) {
                // 堆叠数量达到上限直接跳过
                /*if (stack.isFull()) continue;*/

                int newAmount = stack.getAmount() + itemStack.getAmount();
                int maxCount = stack.getItem().property.getMaxCount();
                if (newAmount <= maxCount) {
                    stack.setAmount(newAmount);
                    return null;
                } else {
                    stack.setAmount(maxCount);
                    itemStack.setAmount(newAmount - maxCount);
                }
            }
        }

        // 查找空位，然后把物品堆叠放进去
        for (int i = 0; i < this.size; ++i) {
            if (this.itemStacks.get(i) == null) {
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
            if (itemStack != null) itemStack.update(delta);
            else this.itemStacks.remove(index);
        });
    }
}

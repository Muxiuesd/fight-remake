package ttk.muxiuesd.world.loottable.common;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

import java.util.function.Supplier;

/**
 * 战利品条目
 * <p>
 * 通用战利品表的随机条目，持有掉落模板 {@link ItemStack}，支持权重与随机数量。
 * 组（互斥关系）由 {@link LootGroup} 承担，条目自身不持有分组信息
 * */
public class LootEntry implements Supplier<ItemStack> {
    private ItemStack itemStack;    //掉落模板：物品 + 数量 + 属性
    private int minAmount;          // 最小数量
    private int maxAmount;          // 最大数量
    private float weight;           // 掉落权重

    private LootEntry (ItemStack itemStack, int minAmount, int maxAmount, float weight) {
        this.itemStack = itemStack;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.weight = weight;
    }

    /* ---------- 固定数量：数量 = itemStack 的 amount ---------- */

    /**
     * 从物品堆叠构建，掉落的数量固定为堆叠的数量
     * */
    public static LootEntry of (ItemStack itemStack, float weight) {
        return new LootEntry(itemStack, itemStack.getAmount(), itemStack.getAmount(), weight);
    }

    /**
     * 从物品构建，掉落的数量固定为 1
     * */
    public static LootEntry of (Item item, float weight) {
        return of(new ItemStack(item, 1), weight);
    }

    /* ---------- 随机数量 ---------- */

    /**
     * 从物品堆叠构建，掉落的数量在最小与最大之间随机
     * */
    public static LootEntry of (ItemStack itemStack, int minAmount, int maxAmount, float weight) {
        return new LootEntry(itemStack, minAmount, maxAmount, weight);
    }

    /**
     * 从物品构建，掉落的数量在最小与最大之间随机
     * */
    public static LootEntry of (Item item, int minAmount, int maxAmount, float weight) {
        return of(new ItemStack(item, 1), minAmount, maxAmount, weight);
    }


    /**
     * 获取物品堆叠（copy过的副本）
     * */
    @Override
    public ItemStack get () {
        int amount = this.minAmount == this.maxAmount
            ? this.itemStack.getAmount()
            : MathUtils.random(this.minAmount, this.maxAmount);
        //复制模板（含属性），避免生成物之间的互相污染，也避免污染战利品表本身
        return this.itemStack.copy(amount);
    }

    public ItemStack getItemStack () {
        return itemStack;
    }

    public LootEntry setItemStack (ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    /**
     * 随机数量
     * */
    public int getRandomAmount () {
        return MathUtils.random(minAmount, maxAmount);
    }

    public int getMinAmount () {
        return minAmount;
    }

    public LootEntry setMinAmount (int minAmount) {
        this.minAmount = minAmount;
        return this;
    }

    public int getMaxAmount () {
        return maxAmount;
    }

    public LootEntry setMaxAmount (int maxAmount) {
        this.maxAmount = maxAmount;
        return this;
    }

    public float getWeight () {
        return weight;
    }

    public LootEntry setWeight (float weight) {
        this.weight = weight;
        return this;
    }
}

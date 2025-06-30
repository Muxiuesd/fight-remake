package ttk.muxiuesd.world.loottable;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * 战利品条目
 * */
public class LootEntry implements Supplier<ItemStack> {
    private String itemId;
    private int minAmount;    // 最小数量
    private int maxAmount;    // 最大数量
    private float weight;       // 掉落权重
    private String dropGroup;   // 掉落组，相同组名不会同时掉落

    public LootEntry (String itemId, int minAmount, int maxAmount, float weight, String dropGroup) {
        this.itemId = itemId;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.weight = weight;
        this.dropGroup = dropGroup;
    }

    @Override
    public ItemStack get () {
        return new ItemStack(Gets.ITEM(this.itemId), this.getRandomAmount());
    }

    public String getItemId () {
        return itemId;
    }

    public LootEntry setItemId (String itemId) {
        this.itemId = itemId;
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

    public String getDropGroup () {
        return dropGroup;
    }

    public LootEntry setDropGroup (String dropGroup) {
        this.dropGroup = dropGroup;
        return this;
    }
}

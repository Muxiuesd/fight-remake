package ttk.muxiuesd.world.loottable;


import ttk.muxiuesd.world.item.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public abstract class LootTable {
    HashMap<String, LootEntry> entriesMap = new LinkedHashMap<>();
    List<LootEntry> entries = new LinkedList<>();
    private int rollCount = 1;  // 抽取次数
    private boolean allowDuplicates = false;    //是否允许重复掉落


    abstract void register (String id, LootEntry lootEntry);

    abstract void unregister (String id);

    abstract List<ItemStack> generate (int luck);

    public int getRollCount () {
        return rollCount;
    }

    public LootTable setRollCount (int rollCount) {
        this.rollCount = rollCount;
        return this;
    }

    public boolean isAllowDuplicates () {
        return allowDuplicates;
    }

    public LootTable setAllowDuplicates (boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
        return this;
    }
}

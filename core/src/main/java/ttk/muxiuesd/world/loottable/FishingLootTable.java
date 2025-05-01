package ttk.muxiuesd.world.loottable;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.List;
import java.util.function.Consumer;

/**
 * 钓鱼战利品表
 * TODO 随机性
 * */
public class FishingLootTable extends LootTable {
    public final String TAG = FishingLootTable.class.getName();

    private FishingLootTable () {}
    private static class Holder {
        public static final FishingLootTable INSTANCE = new FishingLootTable();
    }
    public static FishingLootTable getInstance () {
        return Holder.INSTANCE;
    }


    static {
        FishingLootTable table = getInstance();
        table.register(Fight.getId("fish"),
            new LootEntry(Fight.getId("fish"), 1, 1, 100, "fishing"));
        table.register(Fight.getId("rubbish"),
            new LootEntry(Fight.getId("rubbish"), 1, 1, 300, "fishing"));
    }

    @Override
    public void register (String id, LootEntry entry) {
        if (entries.contains(entry)) {
            int i = entries.indexOf(entry);
            entries.add(i, entry);
            Log.print(TAG, "Id为：" + id + " 的钓鱼战利品表已被注册，则执行覆盖！");
            return;
        }
        entries.add(entry);
        entriesMap.put(id, entry);
    }

    @Override
    public void unregister (String id) {
        if (! entriesMap.containsKey(id)) {
            Log.print(TAG, "Id为：" + id + " 的钓鱼战利品表不存在！");
            throw new IllegalStateException(id + " 不存在！");
        }
        entries.remove(entriesMap.get(id));
        entriesMap.remove(id);
    }

    /**
     * 生成战利品
     * */
    /*public static ItemStack generate (String id) {
        if (!lootTable.containsKey(id)) {
            Log.print(TAG, "Id为：" + id + " 的钓鱼战利品表不存在！");
            throw new IllegalStateException(id + " 不存在！");
        }
        Supplier<? extends ItemStack> supplier = lootTable.get(id);
        return supplier.get();
    }*/

    /**
     * 生成战利品
     * */
    @Override
    public List<ItemStack> generate (int luck) {
        return LootGenerator.generate(this, luck);
    }

    public void fastGenerate (int luck, Consumer<? super ItemStack> action) {
        List<ItemStack> itemStackList = generate(luck);
        itemStackList.forEach(action);
    }
}

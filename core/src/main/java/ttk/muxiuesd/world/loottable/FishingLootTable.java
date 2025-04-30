package ttk.muxiuesd.world.loottable;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.function.Supplier;

/**
 * 钓鱼战利品表
 * TODO 随机性
 * */
public class FishingLootTable {
    public static final String TAG = FishingLootTable.class.getName();
    private static final LinkedHashMap<String, Supplier<? extends ItemStack>> lootTable = new LinkedHashMap<>();

    static {
        register(Fight.getId("fish"), () -> new ItemStack(Gets.ITEM(Fight.getId("fish")), 2));
    }


    public static <T extends ItemStack> void register (String id, Supplier<T> supplier) {
        if (lootTable.containsKey(id)) {
            lootTable.replace(id, supplier);
            Log.print(TAG, "Id为：" + id + " 的钓鱼战利品表已被注册，则执行覆盖！");
            return;
        }
        lootTable.put(id, supplier);
    }

    public static void unregister (String id) {
        if (!lootTable.containsKey(id)) {
            Log.print(TAG, "Id为：" + id + " 的钓鱼战利品表不存在！");
            throw new IllegalStateException(id + " 不存在！");
        }
        lootTable.remove(id);
    }

    /**
     * 生成战利品
     * */
    public static ItemStack generate (String id) {
        if (!lootTable.containsKey(id)) {
            Log.print(TAG, "Id为：" + id + " 的钓鱼战利品表不存在！");
            throw new IllegalStateException(id + " 不存在！");
        }
        Supplier<? extends ItemStack> supplier = lootTable.get(id);
        return supplier.get();
    }
}

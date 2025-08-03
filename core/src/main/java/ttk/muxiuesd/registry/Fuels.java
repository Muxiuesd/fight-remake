package ttk.muxiuesd.registry;

import ttk.muxiuesd.world.item.abs.Item;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 燃料的注册
 * */
public final class Fuels {
    private static final HashMap<Item, Integer> FUELS = new LinkedHashMap<>();

    static {
        register(Items.STICK, 120);
        register(Items.COAL, 300);
    }


    /**
     * @param item 物品
     * @param energy 能量值
     * */
    public static void register(Item item, int energy) {
        FUELS.put(item, energy);
    }

    /**
     * 获取燃料物品对应的能量值
     * @return 没有这个燃料时返回0
     * */
    public static int get (Item item) {
        if (!FUELS.containsKey(item)) {
            return 0;
        }
        return FUELS.get(item);
    }
}

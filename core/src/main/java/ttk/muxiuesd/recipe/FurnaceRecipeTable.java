package ttk.muxiuesd.recipe;

import ttk.muxiuesd.world.item.ItemStack;

import java.util.LinkedHashMap;

/**
 * 熔炉的配方表
 * */
public class FurnaceRecipeTable {
    private static final LinkedHashMap<String, CookingRecipe> table = new LinkedHashMap<>();
    private static final LinkedHashMap<String, CookingRecipe> map = new LinkedHashMap<>();

    public static CookingRecipe register (CookingRecipe recipe) {
        if (contains(recipe.getId())) {
            throw new IllegalArgumentException("id为：" + recipe.getId() + " 的熔炼配方已存在！！！");
        }
        table.put(recipe.getId(), recipe);
        map.put(recipe.getInput().getItem().getID(), recipe);
        return recipe;
    }

    public static CookingRecipe get (String id) {
        if (!table.containsKey(id)) {
            throw new IllegalArgumentException("id为：" + id + " 的熔炼配方不存在！！！");
        }
        return table.get(id);
    }

    /**
     * 根据输入物品的id得到输出物品堆叠
     * */
    public static ItemStack getOutput (String inputItemId) {
        //TODO 更好的获取输出的方法，能精确控制同一物品不同状态的输出结果
        if (!map.containsKey(inputItemId)) {
            return null;
        }
        return map.get(inputItemId).getOutput();
    }

    public static boolean contains (String id) {
        return table.containsKey(id);
    }
}

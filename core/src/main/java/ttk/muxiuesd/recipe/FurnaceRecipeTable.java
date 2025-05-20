package ttk.muxiuesd.recipe;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.LinkedHashMap;

/**
 * 熔炉的配方表
 * */
public class FurnaceRecipeTable {
    private static final LinkedHashMap<String, CookingRecipe> table = new LinkedHashMap<>();//key为熔炼表ID
    private static final LinkedHashMap<String, CookingRecipe> map = new LinkedHashMap<>();//key为输入物品ID

    public static final CookingRecipe RUBBISH = register(new CookingRecipe(
        Fight.getId("recipe_rubbish"), new ItemStack(Gets.ITEM(Fight.getId("stick")), 1)) {
        @Override
        public ItemStack getOutput () {
            return new ItemStack(Gets.ITEM(Fight.getId("rubbish")), 1);
        }
    });


    /**
     * 注册
     * */
    public static CookingRecipe register (CookingRecipe recipe) {
        if (contains(recipe.getId())) {
            throw new IllegalArgumentException("id为：" + recipe.getId() + " 的熔炼配方已存在！！！");
        }
        table.put(recipe.getId(), recipe);
        map.put(recipe.getInput().getItem().getID(), recipe);
        return recipe;
    }

    public static CookingRecipe get (String id) {
        if (!contains(id)) {
            throw new IllegalArgumentException("id为：" + id + " 的熔炼配方不存在！！！");
        }
        return table.get(id);
    }

    /**
     * 根据输入的物品得到输出物品堆叠
     * */
    public static ItemStack getOutput (ItemStack inputStack) {
        //TODO 更好的获取输出的方法，能精确控制同一物品不同状态的输出结果
        if (!has(inputStack)) {
            return null;
        }
        return map.get(inputStack.getItem().getID()).getOutput();
    }

    public static boolean contains (String id) {
        return table.containsKey(id);
    }

    /**
     * 判断是否有这个熔炼输入
     * */
    public static boolean has (ItemStack stack) {
        //TODO 复杂物品判断
        return map.containsKey(stack.getItem().getID());
    }
}

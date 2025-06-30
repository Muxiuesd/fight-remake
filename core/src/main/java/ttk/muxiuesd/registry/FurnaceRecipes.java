package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.recipe.CookingRecipe;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

import java.util.LinkedHashMap;

/**
 * 熔炉的配方表
 * */
public final class FurnaceRecipes {
    private static final LinkedHashMap<Item, CookingRecipe> map = new LinkedHashMap<>();//key为输入的物品

    public static final CookingRecipe RUBBISH = register("recipe_rubbish", Items.STICK, Items.RUBBISH);
    public static final CookingRecipe GLASS = register("recipe_glass", Items.SAND, Items.GLASS);


    /**
     * 普通的配方注册，输入一个就输出一个
     * */
    public static CookingRecipe register (String name, Item input, Item output) {
        return register(new CookingRecipe(Fight.getId(name), new ItemStack(input, 1)) {
            @Override
            public ItemStack getOutput () {
                return new ItemStack(output, 1);
            }
        });
    }

    /**
     * 注册
     * */
    public static CookingRecipe register (CookingRecipe recipe) {
        if (contains(recipe.getId())) {
            throw new IllegalArgumentException("id为：" + recipe.getId() + " 的熔炼配方已存在！！！");
        }
        map.put(recipe.getInput().getItem(), recipe);
        return Registries.COOKING_RECIPE.register(new Identifier(recipe.getId()), recipe);
    }

    public static CookingRecipe get (String id) {
        if (!contains(id)) {
            throw new IllegalArgumentException("id为：" + id + " 的熔炼配方不存在！！！");
        }
        return Registries.COOKING_RECIPE.get(id);
    }

    /**
     * 根据输入的物品得到输出物品堆叠
     * */
    public static ItemStack getOutput (ItemStack inputStack) {
        //TODO 更好的获取输出的方法，能精确控制同一物品不同状态的输出结果
        if (!has(inputStack)) {
            return null;
        }
        //判断物品堆叠属性是否符合要求
        CookingRecipe cookingRecipe = map.get(inputStack.getItem());
        if (!cookingRecipe.match(inputStack)) {
            return null;
        }
        //到这里应该符合要求了
        return cookingRecipe.getOutput();
    }

    public static boolean contains (String id) {
        return Registries.COOKING_RECIPE.contains(id);
    }

    /**
     * 根据输入的物品堆叠的物品判断是否有这个熔炼配方
     * */
    public static boolean has (ItemStack stack) {
        //TODO 复杂物品判断
        return map.containsKey(stack.getItem());
    }
}

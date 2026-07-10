package ttk.muxiuesd.registry;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.recipe.CraftingTableRecipe;
import ttk.muxiuesd.recipe.RecipeParser;
import ttk.muxiuesd.recipe.ShapedCraftingTableRecipe;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

import java.util.HashMap;

/**
 * 所有工作台配方表的注册
 * */
public class CraftingRecipes {
    public static void init () {
        Log.print(CraftingTableRecipe.class.getName(), "注册工作台合成表！");
    }

    public static final CraftingTableRecipe A = registerShaped("a", new ItemStack(Items.COAL),
        "GGG",
        "GGG",
        "SSS",
        PatternPair.of('G', Items.GRASS),
        PatternPair.of('S', Items.STONE)
        );

    public static final CraftingTableRecipe B = registerShaped("a", new ItemStack(Items.COAL),
        "  G",
        " G ",
        "S  ",
        PatternPair.of('G', Items.GRASS),
        PatternPair.of('S', Items.STONE)
    );

    /**
     * 注册有序的工作台配方表，使用字符与物品映射类来快捷配对
     * */
    public static CraftingTableRecipe registerShaped (String name, ItemStack output,
                                                      String pattern1,
                                                      String pattern2,
                                                      String pattern3,
                                                      PatternPair... pairs) {
        HashMap<Character, Item> keys = new HashMap<>();
        for (PatternPair pair : pairs) {
            keys.put(pair.getKey(), pair.getValue());
        }
        return registerShaped(name, pattern1, pattern2, pattern3, keys, output);
    }
    /**
     * 注册有序的工作台配方表,拆分了形状模板的字符串数组
     * */
    public static CraftingTableRecipe registerShaped (String name,
                                                      String pattern1,
                                                      String pattern2,
                                                      String pattern3,
                                                      HashMap<Character, Item> keys,
                                                      ItemStack output) {
        return registerShaped(name, new String[]{pattern1, pattern2, pattern3}, keys, output);
    }

    /**
     * 注册有序的工作台配方表
     * */
    public static CraftingTableRecipe registerShaped (String name,
                                                String[] pattern, HashMap<Character, Item> keys,
                                                ItemStack output) {
        Identifier identifier = Identifier.of(Fight.NAMESPACE, name);
        ShapedCraftingTableRecipe shapedRecipe = RecipeParser.parseShaped(pattern, keys, output);
        return register(identifier, shapedRecipe);
    }


    /**
     * 基础的配方注册
     * @param identifier 注册表id标识符
     * @param recipe 注册表
     * */
    public static CraftingTableRecipe register (Identifier identifier, CraftingTableRecipe recipe) {
        return Registries.CRAFTING_RECIPE_REGISTRY.register(identifier, recipe);
    }


    /**
     * 工具类：字符与物品映射类
     * */
    public static class PatternPair implements ttk.muxiuesd.interfaces.util.Pair<Character, Item> {
        private Character character;
        private Item item;

        public static PatternPair of (Character key, Item value) {
            return new PatternPair(key, value);
        }

        public PatternPair(Character character, Item item) {
            this.character = character;
            this.item = item;
        }

        @Override
        public void set(Character key, Item value) {
            this.character = key;
            this.item = value;
        }

        @Override
        public Character getKey() {
            return this.character;
        }

        @Override
        public Item getValue() {
            return this.item;
        }

        @Override
        public void setKey(Character key) {
            this.character = key;
        }

        @Override
        public void setValue(Item value) {
            this.item = value;
        }
    }

}

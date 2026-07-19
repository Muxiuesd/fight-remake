package ttk.muxiuesd.recipe;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 配方的解释器
 * */
public class RecipeParser {
    public static final String TAG = RecipeParser.class.getName();

    /**
     * 根据形状定义与符号映射创建有序合成配方
     * @param pattern    形状模板，例如 ["WWW","W W","WWW"]
     * @param key        符号->物品  映射
     * @param output     合成产物
     * @return 可直接用于注册的ShapedCraftingRecipe实例
     */
    public static ShapedCraftingTableRecipe parseShaped (String[] pattern,
                                                         HashMap<Character, Item> key,
                                                         ItemStack output) {
        HashMap<Character, String> map = new HashMap<>();
        for (Map.Entry<Character, Item> entry : key.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getID());
        }
        return parseShaped(pattern, map, output);
    }

    /**
     * 根据形状定义与符号映射创建有序合成配方
     * @param pattern    形状模板，例如 ["WWW","W W","WWW"]
     * @param key        符号->物品的ID 映射
     * @param output     合成产物
     * @return 可直接用于注册的ShapedCraftingRecipe实例
     */
    public static ShapedCraftingTableRecipe parseShaped (String[] pattern,
                                                         Map<Character, String> key,
                                                         ItemStack output) {
        //构建3×3的Identifier网格
        Identifier[][] fullGrid = new Identifier[3][3];
        for (int r = 0; r < 3; r++) {
            String row = pattern[r];
            for (int c = 0; c < 3; c++) {
                char ch = row.charAt(c);
                String id = key.get(ch);
                if (ch == ' ') {
                    fullGrid[r][c] = null;
                } else {
                    //检查这个id的物品是否存在
                    Registries.DefaultRegistry<Item> itemsReg = (Registries.DefaultRegistry<Item>) Registries.ITEM;
                    if (!itemsReg.contains(id)) {
                        Log.error(TAG, "配方表解析失败，id为：" + id + " 的物品并未注册！！！");
                        throw new IllegalArgumentException(id);
                    }
                    //根据id字符串查找id标识符
                    HashMap<String, Identifier> idCast = itemsReg.getIDCast();
                    Identifier itemIdentifier = idCast.get(id);
                    fullGrid[r][c] = itemIdentifier;
                }
            }
        }

        //裁剪
        Identifier[][] trimmed = ShapedCraftingTableRecipe.trimGrid(fullGrid);
        if (trimmed.length == 0) throw new IllegalArgumentException("配方不能为空");

        //生成编码集合（原版 + 镜像）
        Set<String> patterns = new HashSet<>();
        patterns.add(ShapedCraftingTableRecipe.encodeShape(trimmed)); //原版
        patterns.add(ShapedCraftingTableRecipe.encodeShapeMirrorHorizontal(trimmed));//水平镜像

        //构造用于UI展示的 inputs 数组（将 3×3 展平为一维）
        ItemStack[] displayInputs = new ItemStack[9];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                Identifier id = fullGrid[r][c];
                displayInputs[r * 3 + c] = (id == null) ?
                    ItemStack.VOID : new ItemStack(Registries.ITEM.get(id), 1);
            }
        }

        return new ShapedCraftingTableRecipe(displayInputs, output, patterns);
    }
}

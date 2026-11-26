package ttk.muxiuesd.recipe;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 *  规则的工作台合成表
 * */
public class ShapedCraftingTableRecipe extends CraftingTableRecipe {
    //预生成的形状编码（原版 + 水平镜像）
    private final Set<String> encodedPatterns;

    /**
     * @param inputs            输入的物品
     * @param output            合成结果
     * @param encodedPatterns   形状的编码（配方有的会左右镜像皆可）
     */
    public ShapedCraftingTableRecipe (ItemStack[] inputs, ItemStack output, Set<String> encodedPatterns) {
        super(inputs, output);

        this.encodedPatterns = Collections.unmodifiableSet(encodedPatterns);
    }

    @Override
    public boolean matches (ItemStack[] inputs) {
        //长度不一致直接可以跳过
        if (inputs.length != 9 || inputs.length != this.getInputs().length) return false;

        //将ItemStack 一维数组转为 3×3 网格，空槽用 null
        Identifier[][] grid = new Identifier[3][3];
        for (int i = 0; i < 9; i++) {
            int r = i / 3;
            int c = i % 3;
            ItemStack stack = inputs[i];
            grid[r][c] = (!stack.isVoid()) ? stack.getItem().getIdentifier() : null;
        }

        //裁剪并编码后与预存编码比较
        Identifier[][] compact = trimGrid(grid);
        if (compact.length == 0) return false; // 全空不匹配
        return this.encodedPatterns.contains(encodeShape(compact));
    }


    /**
     * 裁剪：去掉四周全为 null 的行和列
     * */
    public static Identifier[][] trimGrid (Identifier[][] grid) {
        int minRow = 3, maxRow = -1, minCol = 3, maxCol = -1;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (grid[r][c] != null) {
                    if (r < minRow) minRow = r;
                    if (r > maxRow) maxRow = r;
                    if (c < minCol) minCol = c;
                    if (c > maxCol) maxCol = c;
                }
            }
        }
        if (maxRow == -1) return new Identifier[0][0];

        int rows = maxRow - minRow + 1;
        int cols = maxCol - minCol + 1;
        Identifier[][] result = new Identifier[rows][cols];
        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                result[r - minRow][c - minCol] = grid[r][c];
            }
        }
        return result;
    }

    /**
     * 编码水平翻转后的配方网格
     * */
    public static String encodeShapeMirrorHorizontal (Identifier[][] grid) {
        return encodeShape(mirrorHorizontal(grid));
    }


    /**
     * 将配方网格水平镜像翻转
     * */
    public static Identifier[][] mirrorHorizontal (Identifier[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        Identifier[][] mirrored = new Identifier[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                mirrored[r][c] = grid[r][cols - 1 - c];
            }
        }
        return mirrored;
    }

    /**
     * 将裁剪后的网格编码为字符串（用换行分隔，null → "∅"）
     * */
    public static String encodeShape (Identifier[][] grid) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < grid.length; r++) {
            if (r > 0) sb.append("\n");
            for (int c = 0; c < grid[0].length; c++) {
                Identifier id = grid[r][c];
                sb.append(id == null ? "∅" : id.toString()); // 依赖 Identifier 的 toString()
            }
        }
        return sb.toString();
    }
}

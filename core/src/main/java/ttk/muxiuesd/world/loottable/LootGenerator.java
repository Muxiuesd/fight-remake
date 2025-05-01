package ttk.muxiuesd.world.loottable;

import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 战利品生成器
 * */
public class LootGenerator {
    /**
     * 执行掉落计算
     * @param table 需要掉落出物品的表
     * @param luck 幸运值
     * */
    public static List<ItemStack> generate(LootTable table, int luck) {
        List<ItemStack> result = new ArrayList<>();
        Random rand = new Random();

        // 按组分类条目
        Map<String, List<LootEntry>> groupedEntries = table.entries.stream().collect(Collectors.groupingBy(LootEntry::getDropGroup));

        for (int i = 0; i < table.getRollCount(); i++) {
            groupedEntries.forEach((group, entries) -> {
                if (entries.isEmpty()) return;

                // 计算总权重
                float totalWeight = entries.stream()
                    .map(e -> calculateEffectiveWeight(e, luck))
                    .reduce(0f, Float::sum);

                // 随机选择
                float random = rand.nextFloat() * totalWeight;
                float accumulated = 0;

                for (LootEntry entry : entries) {
                    accumulated += calculateEffectiveWeight(entry, luck);
                    if (random <= accumulated) {
                        addLootEntry(result, entry, rand);
                        if (!table.isAllowDuplicates()) entries.remove(entry);
                        break;
                    }
                }
            });
        }
        return result;
    }

    // 带幸运值的权重计算
    private static float calculateEffectiveWeight(LootEntry entry, int luck) {
        float baseWeight = entry.getWeight();
        float luckFactor = 1 + (luck / 100f); // 每点幸运增加1%权重
        return baseWeight * luckFactor;
    }

    private static void addLootEntry(List<ItemStack> result, LootEntry entry, Random rand) {
        int quantity = entry.getMinAmount() + rand.nextInt(entry.getMaxAmount() - entry.getMinAmount() + 1);
        result.add(new ItemStack(Gets.ITEM(entry.getItemId()), quantity));
    }
}

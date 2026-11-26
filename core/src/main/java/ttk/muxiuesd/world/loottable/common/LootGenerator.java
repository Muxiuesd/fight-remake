package ttk.muxiuesd.world.loottable.common;

import ttk.muxiuesd.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * 战利品生成器
 * <p>
 * 负责执行带权重的随机抽取，是战利品表的"抽取引擎"。
 *
 * <h2>核心概念</h2>
 * <ul>
 *   <li><b>LootEntry（条目）</b>：一次抽取中的最小单元，持有一个掉落模板 {@link ttk.muxiuesd.world.item.ItemStack}
 *       （含物品、数量、属性），并带有权重与可选的随机数量区间。{@link LootEntry#get()} 返回模板副本。</li>
 *   <li><b>LootGroup（组）</b>：若干条目的集合，代表一组<b>互斥</b>的掉落——组内条目不能同时掉落。
 *       组名仅作占位标识，不参与抽取逻辑。</li>
 *   <li><b>战利品表（如 EntityDeathLootTable）</b>：持有若干组，构建时确定分组结构，运行时不再重新分类。</li>
 * </ul>
 *
 * <h2>抽取流程</h2>
 * <ol>
 *   <li>遍历战利品表中的每个组（组与组之间完全独立，互不影响）；</li>
 *   <li>对每个组执行 <b>rollCount</b> 次抽取：每次先对该组当前剩余条目求权重总和 totalWeight，
 *       再在 [0, totalWeight) 内取随机数，按条目权重依次累加，命中的条目即本次抽中的掉落物；</li>
 *   <li>命中后，若 <b>allowDuplicates=false</b>（默认），该条目会从本组的"剩余池"中移除，
 *       保证一次生成内不会重复掉落同一个条目；若为 true 则允许重复掉落。</li>
 * </ol>
 *
 * <h2>典型效果</h2>
 * <ul>
 *   <li><b>同组多条目 + rollCount=1</b>：从组内按权重随机抽 1 个（如僵尸从垃圾/木棍/土豆中掉 1 样）；</li>
 *   <li><b>同组多条目 + rollCount=2</b>：抽 2 次且默认不重复，相当于从组内随机掉 2 样（权重影响先后概率）；</li>
 *   <li><b>多组</b>：每组各自必出（rollCount≥1 时每组必抽 1 次），常用于"必掉经验球 + 随机掉落物"等场景。</li>
 * </ul>
 *
 * <h2>幸运值</h2>
 * 幸运值 <b>luck</b> 以百分比放大每组权重（luck=100 即权重×2），目前实体死亡掉落固定传 0，未接入。
 * */
public class LootGenerator {
    /**
     * 执行掉落计算
     * @param groups 参与抽取的组列表，每组独立抽取
     * @param rollCount 每组抽取次数
     * @param allowDuplicates 是否允许同一条目在一次生成中重复掉落
     * @param luck 幸运值
     * */
    public static List<ItemStack> generate(List<LootGroup> groups, int rollCount, boolean allowDuplicates, int luck) {
        List<ItemStack> result = new ArrayList<>();
        Random rand = new Random();

        for (LootGroup group : groups) {
            //工作副本：去重移除时不影响组本身，且在一次生成内跨 roll 生效
            List<LootEntry> groupEntries = new ArrayList<>(group.getEntries());
            for (int i = 0; i < rollCount; i++) {
                if (groupEntries.isEmpty()) break;

                // 计算总权重
                float totalWeight = groupEntries.stream()
                    .map(e -> calculateEffectiveWeight(e, luck))
                    .reduce(0f, Float::sum);

                // 随机选择
                float random = rand.nextFloat() * totalWeight;
                float accumulated = 0;

                Iterator<LootEntry> iterator = groupEntries.iterator();
                while (iterator.hasNext()) {
                    LootEntry entry = iterator.next();
                    accumulated += calculateEffectiveWeight(entry, luck);
                    if (random <= accumulated) {
                        addLootEntry(result, entry);
                        if (!allowDuplicates) iterator.remove();
                        break;
                    }
                }
            }
        }
        return result;
    }

    // 带幸运值的权重计算
    private static float calculateEffectiveWeight(LootEntry entry, int luck) {
        float baseWeight = entry.getWeight();
        float luckFactor = 1 + (luck / 100f); // 每点幸运增加1%权重
        return baseWeight * luckFactor;
    }

    private static void addLootEntry(List<ItemStack> result, LootEntry entry) {
        result.add(entry.get());
    }
}

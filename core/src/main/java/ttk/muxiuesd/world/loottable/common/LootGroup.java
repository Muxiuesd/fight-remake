package ttk.muxiuesd.world.loottable.common;

import java.util.ArrayList;
import java.util.List;

/**
 * 战利品组
 * <p>
 * 一组互斥的条目（组内每次按权重抽出一个），
 * 分组在构建战利品表时确定，而不是生成时动态分类。
 * name 仅作占位标识，不参与逻辑
 * */
public class LootGroup {
    /**
     * 便捷构建：组名 + 若干条目（无条件，永远参与抽取）
     * */
    public static LootGroup of (String name, LootEntry... entries) {
        return of(LootCondition.ALWAYS, name, entries);
    }

    /**
     * 便捷构建：生成条件 + 组名 + 若干条目
     * <p>
     * 条件不满足时整组不参与抽取，由掉落表在生成前统一过滤
     * */
    public static LootGroup of (LootCondition condition, String name, LootEntry... entries) {
        //用可变列表：{@link #add(LootEntry)}需要能往里加
        return new LootGroup(condition, name, new ArrayList<>(List.of(entries)));
    }


    private final LootCondition condition;      //生成条件
    private final String name;                  //组名，占位标识
    private final List<LootEntry> entries;      //所持有的条目

    public LootGroup (String name, List<LootEntry> entries) {
        this(LootCondition.ALWAYS, name, entries);
    }

    public LootGroup (LootCondition condition, String name, List<LootEntry> entries) {
        this.condition = condition;
        this.name = name;
        this.entries = entries;
    }

    public LootGroup add (LootEntry entry) {
        this.entries.add(entry);
        return this;
    }

    public LootCondition getCondition () {
        return this.condition;
    }

    public List<LootEntry> getEntries () {
        return this.entries;
    }

    public String getName () {
        return this.name;
    }

    public boolean isEmpty () {
        return this.entries.isEmpty();
    }
}

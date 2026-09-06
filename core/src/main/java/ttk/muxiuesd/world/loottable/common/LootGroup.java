package ttk.muxiuesd.world.loottable.common;

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
     * 便捷构建：组名 + 若干条目
     * */
    public static LootGroup of (String name, LootEntry... entries) {
        return new LootGroup(name, List.of(entries));
    }


    private final String name;                  //组名，占位标识
    private final List<LootEntry> entries;      //所持有的条目

    public LootGroup (String name, List<LootEntry> entries) {
        this.name = name;
        this.entries = entries;
    }

    public LootGroup add (LootEntry entry) {
        this.entries.add(entry);
        return this;
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

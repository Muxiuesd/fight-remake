package ttk.muxiuesd.interfaces.world.block;

import ttk.muxiuesd.interfaces.world.loottable.LootTable;

/**
 * 方块相关的战利品表的接口
 * @param <C> 条件接口
 * */
public interface BlockLootTable<C extends BlockLootTable.Conditions<C>> extends LootTable<C> {
}

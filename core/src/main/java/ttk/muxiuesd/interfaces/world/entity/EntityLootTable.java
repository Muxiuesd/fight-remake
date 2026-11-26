package ttk.muxiuesd.interfaces.world.entity;

import ttk.muxiuesd.interfaces.world.loottable.LootTable;

/**
 * 实体相关的战利品表的接口
 * @param <C> 条件接口
 * */
public interface EntityLootTable<C extends EntityLootTable.Conditions<C>> extends LootTable<C> {
}

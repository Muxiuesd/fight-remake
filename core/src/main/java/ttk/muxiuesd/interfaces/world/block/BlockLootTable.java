package ttk.muxiuesd.interfaces.world.block;

import ttk.muxiuesd.world.World;

/**
 * 方块相关的战利品表的接口
 * */
public interface BlockLootTable<C extends BlockLootTable.Condition> {
    /**
     * 生成战利品
     * */
    void generate (World world, C condition);

    /**
     * 条件类
     * */
    class Condition {}
}

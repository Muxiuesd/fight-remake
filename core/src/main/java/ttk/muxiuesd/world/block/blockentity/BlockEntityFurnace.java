package ttk.muxiuesd.world.block.blockentity;

import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;

/**
 * 熔炉
 * */
public class BlockEntityFurnace extends BlockEntity {
    public BlockEntityFurnace (Block block, BlockPos blockPos) {
        super(block, blockPos, 3);
    }
}

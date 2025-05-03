package ttk.muxiuesd.world.block.abs;

import ttk.muxiuesd.interfaces.Tickable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.world.block.BlockPos;

/**
 * 方块实体
 * */
public abstract class BlockEntity implements Updateable, Tickable {
    private Block block;
    private BlockPos blockPos;

    public BlockEntity () {
    }


    /**
     * 方块实体每帧更新逻辑
     * */
    @Override
    public void update (float delta) {
    }

    /**
     * 方块实体每tick的更新逻辑
     * */
    @Override
    public void tick (float delta) {
    }

    public Block getBlock () {
        return block;
    }

    public BlockEntity setBlock (Block block) {
        this.block = block;
        return this;
    }

    public BlockPos getBlockPos () {
        return blockPos;
    }

    public BlockEntity setBlockPos (BlockPos blockPos) {
        this.blockPos = blockPos;
        return this;
    }
}

package ttk.muxiuesd.world.block.blockentity;

import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.BlockEntity;

public class BlockEntityProvider<T extends BlockEntity> {
    private final String id;
    private final Factory<T> factory;

    public BlockEntityProvider (String id, Factory<T> factory) {
        this.id = id;
        this.factory = factory;
    }

    public String getId () {
        return this.id;
    }

    public T create (BlockPos pos) {
        return this.factory.create(pos);
    }

    @FunctionalInterface
    public interface Factory<T> {
        T create (BlockPos blockPos);
    }
}

package ttk.muxiuesd.world.block.blockentity;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.BlockEntity;

/**
 * 方块实体的提供者
 * <p>
 * 同一种方块实体持有相同的标识符
 * */
public class BlockEntityProvider<T extends BlockEntity> {
    private Identifier identifier;
    private final Factory<T> factory;

    public BlockEntityProvider (Identifier identifier, Factory<T> factory) {
        this.identifier = identifier;
        this.factory = factory;
    }

    public String getID () {
        return this.identifier.getID();
    }

    public Identifier getIdentifier () {
        return this.identifier;
    }

    public BlockEntityProvider<T> setIdentifier (Identifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public BlockEntityProvider<T> setID (String id) {
        this.identifier.setID(id);
        return this;
    }

    /**
     * 空参构造
     * */
    public T create () {
        return this.create(new BlockPos());
    }

    public T create (BlockPos pos) {
        T blockEntity = this.factory.create(pos);
        //让实例持有这个提供者，同一种方块实体持有相同的提供者（相同的id）
        blockEntity.setProvider(this);
        return blockEntity;
    }

    @FunctionalInterface
    public interface Factory<T> {
        T create (BlockPos blockPos);
    }
}

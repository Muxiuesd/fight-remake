package ttk.muxiuesd.world.block.abs;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;

/**
 * 带有方块实体的方块
 * */
public abstract class BlockWithEntity<T extends BlockWithEntity<?, ?>, E extends BlockEntity> extends Block {
    public BlockWithEntity (Property property, String textureId) {
        super(property, textureId);
    }

    public BlockWithEntity (Property property, String textureId, String texturePath) {
        super(property, textureId, texturePath);
    }

    /**
     * 方块被放置后产生自己的方法
     * */
    public abstract T createSelf();

    /**
     * 产生方块实体
     * */
    public abstract E createBlockEntity(BlockPos blockPos, World world);
}

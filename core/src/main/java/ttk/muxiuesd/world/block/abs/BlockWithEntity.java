package ttk.muxiuesd.world.block.abs;

/**
 * 带有方块实体的方块
 * */
public abstract class BlockWithEntity extends Block{
    public BlockWithEntity (Property property, String textureId) {
        super(property, textureId);
    }

    public BlockWithEntity (Property property, String textureId, String texturePath) {
        super(property, textureId, texturePath);
    }

    /**
     * 产生方块实体
     * */
    public abstract BlockEntity createBlockEntity(Block block);
}

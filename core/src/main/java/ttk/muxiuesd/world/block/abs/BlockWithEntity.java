package ttk.muxiuesd.world.block.abs;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.cat.CAT;

/**
 * 带有方块实体的方块
 * */
public abstract class BlockWithEntity extends Block {
    //方块对应的方块实体
    private BlockEntity blockEntity;

    public BlockWithEntity (Property property, String textureId) {
        super(property, textureId);
    }

    public BlockWithEntity (Property property, String textureId, String texturePath) {
        super(property, textureId, texturePath);
    }

    @Override
    public void writeCAT (CAT cat) {
        super.writeCAT(cat);
        //写入方块实体的属性
        this.getBlockEntity().writeCAT(cat);
    }

    @Override
    public void readCAT (JsonValue values) {
        super.readCAT(values);
        //让方块实体读取属性
        this.getBlockEntity().readCAT(values);
    }

    /**
     * 方块被放置后产生自己的方法
     * */
    public abstract BlockWithEntity createSelf();

    /**
     * 产生方块实体
     * */
    public abstract BlockEntity createBlockEntity(BlockPos blockPos, World world);

    public BlockEntity getBlockEntity () {
        return this.blockEntity;
    }

    public BlockWithEntity setBlockEntity (BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        return this;
    }
}

package ttk.muxiuesd.world.block.abs;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.interfaces.ICatData;
import ttk.muxiuesd.interfaces.render.world.block.BlockEntityRenderer;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.cat.CatsHolder;

/**
 * 带有方块实体的方块
 * <p>
 * 这种方块每一个都是一个单独的实例
 * */
public abstract class BlockWithEntity extends Block implements ICatData {
    //方块对应的方块实体实例
    private BlockEntity blockEntity;

    public BlockWithEntity (Property property, String textureId) {
        super(property, textureId);
    }

    public BlockWithEntity (Property property, String textureId, String texturePath) {
        super(property, textureId, texturePath);
    }

    /**
     * 方块被放置后产生自己的方法（产生单独实例）
     * */
    public abstract BlockWithEntity createSelf ();

    /**
     * 产生方块实体实例
     * */
    public abstract BlockEntity createBlockEntity (BlockPos blockPos, World world);

    @Override
    public void writeCatData (CatsHolder holder) {
        //写入方块实体的属性
        this.getBlockEntity().writeCatData(holder);
    }

    @Override
    public void readCatData (JsonValue values) {
        //让方块实体读取属性
        this.getBlockEntity().readCatData(values);
    }

    /**
     * 获取它持有的方块实体
     * */
    public BlockEntity getBlockEntity () {
        return this.blockEntity;
    }

    public BlockWithEntity setBlockEntity (BlockEntity blockEntity) {
        if (blockEntity != null) this.blockEntity = blockEntity;
        return this;
    }

    /**
     * 获取持有的方块实体的渲染器
     * */
    public BlockEntityRenderer<? extends BlockEntity> getBlockEntityRenderer () {
        return new BlockEntityRenderer.StandardRenderer<>();
    }
}



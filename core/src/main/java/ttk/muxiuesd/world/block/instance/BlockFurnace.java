package ttk.muxiuesd.world.block.instance;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.block.blockentity.BlockEntityFurnace;

/**
 * 熔炉方块
 * */
public class BlockFurnace extends BlockWithEntity {
    public BlockFurnace (Property property, String textureId, String texturePath) {
        super(property, textureId, texturePath);
    }

    @Override
    public BlockEntity createBlockEntity (BlockPos blockPos, World world) {
        return new BlockEntityFurnace(this, blockPos);
    }
}

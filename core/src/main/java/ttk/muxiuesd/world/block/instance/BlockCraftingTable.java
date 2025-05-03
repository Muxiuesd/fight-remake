package ttk.muxiuesd.world.block.instance;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.block.blockentity.BlockEntityCraftingTable;

/**
 * 工作台
 * */
public class BlockCraftingTable extends BlockWithEntity {
    public BlockCraftingTable () {
        super(new Property().setFriction(0.7f),
            Fight.getId("crafting_table"),
            Fight.BlockTexturePath("crafting_table.png"));
    }


    @Override
    public BlockEntity createBlockEntity (BlockPos blockPos, World world) {
        return new BlockEntityCraftingTable(this, blockPos);
    }
}

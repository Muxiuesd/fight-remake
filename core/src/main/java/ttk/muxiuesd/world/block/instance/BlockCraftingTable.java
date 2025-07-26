package ttk.muxiuesd.world.block.instance;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.block.blockentity.BlockEntityCraftingTable;

/**
 * 工作台
 * */
public class BlockCraftingTable extends BlockWithEntity<BlockCraftingTable, BlockEntityCraftingTable> {
    public BlockCraftingTable () {
        super(createProperty().setFriction(0.7f),
            Fight.getId("crafting_table"),
            Fight.BlockTexturePath("crafting_table.png"));
    }


    @Override
    public BlockCraftingTable createSelf () {
        BlockCraftingTable block = new BlockCraftingTable();
        block.setID(getID());
        return block;
    }

    @Override
    public BlockEntityCraftingTable createBlockEntity (BlockPos blockPos, World world) {
        return new BlockEntityCraftingTable(world, this, blockPos);
    }
}

package ttk.muxiuesd.world.block.instance;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.Codecable;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.serialization.codecs.builders.BlockWithEntityCodecBuilder;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.block.blockentity.BlockEntityCraftingTable;

/**
 * 工作台
 * */
public class BlockCraftingTable extends BlockWithEntity implements Codecable<BlockCraftingTable> {
    public static final Codec<BlockCraftingTable> CODEC = BlockWithEntityCodecBuilder.create(
        BlockCraftingTable::new,
        BlockEntityCraftingTable.CODEC,
        builder -> builder
    );

    public BlockCraftingTable () {
        super(createProperty().setFriction(0.7f),
            Fight.ID("crafting_table"),
            Fight.BlockTexturePath("crafting_table.png"));
    }


    @Override
    public BlockCraftingTable createSelf () {
        BlockCraftingTable block = new BlockCraftingTable();
        block.setIdentifier(getIdentifier());
        return block;
    }

    @Override
    public BlockEntityCraftingTable createBlockEntity (BlockPos blockPos, World world) {
        return new BlockEntityCraftingTable(blockPos);
    }

    @Override
    public Codec<BlockCraftingTable> getCodec () {
        return CODEC;
    }
}

package ttk.muxiuesd.world.block.instance;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.Codecable;
import ttk.muxiuesd.serialization.codecs.builders.BlockWithEntityCodecBuilder;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.Block;
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
        super(Block.createProperty().setFriction(0.7f));
    }


    @Override
    public BlockCraftingTable createSelf () {
        BlockCraftingTable block = new BlockCraftingTable();
        block.setIdentifier(getIdentifier());
        //渲染器是注册时绑在原型上的，每格独享的实例要自己带一份，否则渲染热路径读到null
        block.setRenderer(this.getRenderer());
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

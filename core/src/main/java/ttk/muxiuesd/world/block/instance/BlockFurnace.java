package ttk.muxiuesd.world.block.instance;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.Codecable;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.serialization.codecs.BlockWithEntityCodecBuilder;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.block.blockentity.BlockEntityFurnace;
import ttk.muxiuesd.world.cat.CatBoolean;
import ttk.muxiuesd.world.cat.CatsHolder;

/**
 * 熔炉方块
 * */
public class BlockFurnace extends BlockWithEntity implements Codecable<BlockFurnace> {

    /*public final Codec<BlockFurnace> CODEC1 = CodecBuilder.<BlockFurnace>create()
        .field("id", Block::getID, (a, b)-> {}, Codec.STRING)
        .field("is_working", BlockFurnace::isWorking, BlockFurnace::setWorking, Codec.BOOL)
        .field("block_entity",
            BlockFurnace::getBlockEntity,
            BlockFurnace::setBlockEntity,
            BlockEntityFurnace.CODEC
        )
        .field("property",
            block -> {
                //调用cats写入
                block.writeCatData(block.getProperty().get(PropertyTypes.CATS));
                return block.getProperty();
            },
            (block, property) -> {
                //读取cat，同时会读取方块实体的cat
                //block.readCatData(property.get(PropertyTypes.CATS));
            },
            Property.CODEC
        )
        .noArgFactory(BlockFurnace::new);*/

    public static final Codec<BlockFurnace> CODEC = BlockWithEntityCodecBuilder.create(
        BlockFurnace::new,
        BlockEntityFurnace.CODEC,
        builder -> builder
                .field("is_working", BlockFurnace::isWorking, BlockFurnace::setWorking, Codec.BOOL)
    );


    private TextureRegion workingTexture;
    private boolean isWorking = false;

    public BlockFurnace () {
        super(createProperty().setFriction(0.5f), Fight.ID("furnace"), Fight.BlockTexturePath("furnace.png"));
        this.workingTexture = Util.loadTextureRegion(Fight.ID("furnace_on"), Fight.BlockTexturePath("furnace_on.png"));
    }

    @Override
    public void writeCatData (CatsHolder holder) {
        super.writeCatData(holder);
        holder.put("is_working", new CatBoolean(this.isWorking));
    }

    @Override
    public void readCatData (JsonValue values) {
        super.readCatData(values);
        this.isWorking = values.getBoolean("is_working", false);
    }

    @Override
    public BlockFurnace createSelf () {
        BlockFurnace blockFurnace = new BlockFurnace();
        blockFurnace.setIdentifier(getIdentifier());
        return blockFurnace;
    }

    @Override
    public BlockEntityFurnace createBlockEntity (BlockPos blockPos, World world) {
        return new BlockEntityFurnace(blockPos);
    }

    public boolean isWorking () {
        return this.isWorking;
    }

    public void setWorking (boolean working) {
        isWorking = working;
    }

    @Override
    public Codec<BlockFurnace> getCodec () {
        return CODEC;
    }
}

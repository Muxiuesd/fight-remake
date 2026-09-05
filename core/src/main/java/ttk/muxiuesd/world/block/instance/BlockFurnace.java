package ttk.muxiuesd.world.block.instance;

import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.Codecable;
import ttk.muxiuesd.serialization.codecs.builders.BlockWithEntityCodecBuilder;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.block.blockentity.BlockEntityFurnace;
import ttk.muxiuesd.world.cat.CatBoolean;
import ttk.muxiuesd.world.cat.CatsHolder;

/**
 * 熔炉方块
 * <p>
 * 贴图由熔炉渲染器持有（含燃烧贴图）
 * */
public class BlockFurnace extends BlockWithEntity implements Codecable<BlockFurnace> {
    public static final Codec<BlockFurnace> CODEC = BlockWithEntityCodecBuilder.create(
        BlockFurnace::new,
        BlockEntityFurnace.CODEC,
        builder -> builder
    );


    private boolean isWorking = false;

    public BlockFurnace () {
        super(new Property().setFriction(0.5f));
    }


    @Override
    public void writeCatData (CatsHolder holder) {
        super.writeCatData(holder);
        holder.put("is_working", new CatBoolean(this.isWorking));
    }

    @Override
    public void readCatData (CatsHolder holder) {
        super.readCatData(holder);
        this.isWorking = holder.getBoolean("is_working", false);
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

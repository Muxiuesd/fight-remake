package ttk.muxiuesd.world.block.instance;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.Fight;
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
public class BlockFurnace extends BlockWithEntity {
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
}

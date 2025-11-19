package ttk.muxiuesd.world.block.instance;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.block.blockentity.BlockEntityFurnace;
import ttk.muxiuesd.world.cat.CAT;

/**
 * 熔炉方块
 * */
public class BlockFurnace extends BlockWithEntity {
    public static final BlockRenderer<BlockFurnace> RENDERER = (batch, block, context) -> {
        if (!block.isWorking) {
            batch.draw(block.getTextureRegion(),
                context.x, context.y,
                context.originX, context.originY,
                context.width, context.height,
                context.scaleX, context.scaleY,
                context.rotation);
        }else{
            batch.draw(block.workingTexture,
                context.x, context.y,
                context.originX, context.originY,
                context.width, context.height,
                context.scaleX, context.scaleY,
                context.rotation);
        }
    };


    private TextureRegion workingTexture;
    private boolean isWorking = false;

    public BlockFurnace () {
        super(createProperty().setFriction(0.5f), Fight.ID("furnace"), Fight.BlockTexturePath("furnace.png"));
        this.workingTexture = loadTextureRegion(Fight.ID("furnace_on"), Fight.BlockTexturePath("furnace_on.png"));
    }

    @Override
    public void writeCAT (CAT cat) {
        super.writeCAT(cat);
        cat.set("is_working", this.isWorking);
    }

    @Override
    public void readCAT (JsonValue values) {
        super.readCAT(values);
        this.isWorking = values.getBoolean("is_working");
    }

    @Override
    public void draw (Batch batch, float x, float y) {
        if (this.textureIsValid() && !this.isWorking) {
            batch.draw(this.textureRegion,
                x, y,
                this.originX, this.originY,
                this.width, this.height,
                this.scaleX, this.scaleY,
                this.rotation);
        }else if (this.workingTexture != null && this.isWorking) {
            batch.draw(this.workingTexture,
                x, y,
                this.originX, this.originY,
                this.width, this.height,
                this.scaleX, this.scaleY,
                this.rotation);
        }
    }

    @Override
    public BlockFurnace createSelf () {
        BlockFurnace blockFurnace = new BlockFurnace();
        blockFurnace.setID(getID());
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

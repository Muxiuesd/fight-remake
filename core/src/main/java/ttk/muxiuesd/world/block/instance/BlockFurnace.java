package ttk.muxiuesd.world.block.instance;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.block.blockentity.BlockEntityFurnace;

/**
 * 熔炉方块
 * */
public class BlockFurnace extends BlockWithEntity {
    private TextureRegion workingTexture;
    private boolean isWorking = false;

    public BlockFurnace () {
        super(new Property().setFriction(1f), Fight.getId("furnace"), Fight.BlockTexturePath("furnace.png"));
        this.workingTexture = loadTextureRegion(Fight.getId("furnace_on"), Fight.BlockTexturePath("furnace_on.png"));
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
    public BlockEntity createBlockEntity (BlockPos blockPos, World world) {
        return new BlockEntityFurnace(world,this, blockPos);
    }

    public boolean isWorking () {
        return this.isWorking;
    }

    public void setWorking (boolean working) {
        isWorking = working;
    }
}

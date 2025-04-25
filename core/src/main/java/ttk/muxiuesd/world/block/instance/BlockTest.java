package ttk.muxiuesd.world.block.instance;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.block.BlockSoundsID;
import ttk.muxiuesd.world.block.abs.Block;

public class BlockTest extends Block {
    public BlockTest() {
        super(new Property().setFriction(1.0f).setSounds(BlockSoundsID.DEFAULT),
            Fight.getId("block_test"),
            Fight.BlockTexturePath("block_test.png"));
        //textureRegion = new TextureRegion(AssetsLoader.getInstance().get("block/block_test.png", Texture.class));
    }
}

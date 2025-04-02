package ttk.muxiuesd.world.block.instance;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.block.abs.Block;

public class BlockTest extends Block {
    public BlockTest() {
        super(new Property().setFriction(1.0f),
            Fight.getId("test"),
            Fight.getBlockTexture("block_test.png"));
        //textureRegion = new TextureRegion(AssetsLoader.getInstance().get("block/block_test.png", Texture.class));
    }
}

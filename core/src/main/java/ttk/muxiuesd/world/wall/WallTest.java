package ttk.muxiuesd.world.wall;

import ttk.muxiuesd.Fight;

/**
 * 测试用墙体
 * */
public class WallTest extends Wall{
    public WallTest() {
        super(createProperty().setFriction(1f),
            Fight.getId("test_wall"),
            Fight.BlockTexturePath("block_test.png"));
        //textureRegion = new TextureRegion(AssetsLoader.getInstance().get("block/block_test.png", Texture.class));
    }
}

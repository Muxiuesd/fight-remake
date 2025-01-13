package ttk.muxiuesd.world.block.instance;


import ttk.muxiuesd.world.block.Block;

public class BlockStone extends Block {
    public BlockStone() {
        super(new Property().setFriction(0.95f), "block/stone.png");
        //textureRegion = new TextureRegion(AssetsLoader.getInstance().get("block/stone.png", Texture.class));
    }
}

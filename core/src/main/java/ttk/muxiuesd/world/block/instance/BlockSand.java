package ttk.muxiuesd.world.block.instance;


import ttk.muxiuesd.world.block.Block;

public class BlockSand extends Block {
    public BlockSand() {
        super(new Block.Property().setFriction(0.9f),"block/sand.png");
        //textureRegion = new TextureRegion(AssetsLoader.getInstance().get("block/sand.png", Texture.class));
    }
}

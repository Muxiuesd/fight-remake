package ttk.muxiuesd.world.block.instance;


import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 石头方块
 * */
public class BlockStone extends Block {
    public BlockStone() {
        super(Block.DEFAULT_PROPERTY,
            Fight.getId("stone"),
            Fight.BlockTexturePath("stone.png"));
        //textureRegion = new TextureRegion(AssetsLoader.getInstance().get("block/stone.png", Texture.class));
    }
}

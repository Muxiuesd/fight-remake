package ttk.muxiuesd.world.block.instance;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 普通方块的模板
 * */
public class CommonBlock extends Block {
    /**
     * 贴图材质路径直接在 {@link Fight#BLOCK_TEXTURE_ROOT} 下面的方块
     * */
    public CommonBlock (String name, Property property) {
        this(name, name, property);
    }

    /**
     * 贴图材质路径在 {@link Fight#BLOCK_TEXTURE_ROOT} 的子文件夹中的方块
     * */
    public CommonBlock(String name, String pathName, Property property) {
        super(property, Fight.ID(name), Fight.BlockTexturePath(pathName + ".png"));
    }
}

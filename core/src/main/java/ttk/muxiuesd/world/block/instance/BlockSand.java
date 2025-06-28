package ttk.muxiuesd.world.block.instance;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioReg;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 沙子方块
 * */
public class BlockSand extends Block {
    public BlockSand() {
        super(new Block.Property()
                .setFriction(0.9f)
                .setSounds(AudioReg.SAND),
            Fight.getId("sand"),
            Fight.BlockTexturePath("sand.png"));
    }
}

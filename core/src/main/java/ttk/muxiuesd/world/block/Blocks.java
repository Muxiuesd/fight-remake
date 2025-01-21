package ttk.muxiuesd.world.block;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.world.block.instance.*;

/**
 * 方块注册
 * */
public class Blocks {
    static Registrant<Block> registrant = RegistrantGroup.getRegistrant(Fight.NAMESPACE, Block.class);
    static {
        register("block_test", new BlockTest());
        register("grass", new BlockGrass());
        register("stone", new BlockStone());
        register("sand", new BlockSand());
        register("water", new BlockWater());
    }

    private static void register (String id, Block block) {
        registrant.register(id, block);
        return;
    }
}

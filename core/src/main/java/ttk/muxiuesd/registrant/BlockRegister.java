package ttk.muxiuesd.registrant;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.block.Block;
import ttk.muxiuesd.world.block.instance.*;

/**
 * 方块注册
 * */
public class BlockRegister {
    static Registrant<Block> registrant = RegistrantGroup.getRegistrant(Fight.NAMESPACE, Block.class);
    static {
        register("block_test", new BlockTest());
        register("grass", new BlockGrass());
        register("stone", new BlockStone());
        register("sand", new BlockSand());
        register("water", new BlockWater());
    }

    private static void register (String name, Block block) {
        registrant.register(name, block);
    }
}

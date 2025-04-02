package ttk.muxiuesd.mod.api.world;

import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * modAPI：模组方块相关
 * */
public class ModBlocks {

    public static Block getBlock (String id) {
        String[] parts = id.split(":");
        Registrant<Block> otherReg = RegistrantGroup.getRegistrant(parts[0], Block.class);
        return otherReg.get(parts[1]);
    }
}

package ttk.muxiuesd.world.block;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.instance.*;
import ttk.muxiuesd.world.item.ItemsReg;
import ttk.muxiuesd.world.item.instence.BlockItem;

import java.util.function.Supplier;

/**
 * 方块注册
 * */
public class BlocksReg {
    public static final String TAG = BlocksReg.class.getName();
    public static final Registrant<Block> registrant = RegistrantGroup.getRegistrant(Fight.NAMESPACE, Block.class);

    public static void registerAllBlocks () {
        Log.print(TAG, "游戏方块注册完毕");
    }

    public static final Block TEST_BLOCK = register("block_test", BlockTest::new);
    public static final Block GRASS = register("grass", BlockGrass::new);
    public static final Block STONE = register("stone", BlockStone::new);
    public static final Block SAND = register("sand", BlockSand::new);
    public static final Block WATER = register("water", BlockWater::new);

    public static final Block CRAFTING_TABLE = register("crafting_table", BlockCraftingTable::new);

    public static Block register (String name, final Supplier<Block> supplier) {
        Block block = registrant.register(name, supplier);
        String id = Fight.getId(name);
        //注册方块物品
        ItemsReg.register(name, () -> new BlockItem(id, id));
        return block;
    }

    public static Block newBlock (String name) {
        return registrant.get(name);
    }


}

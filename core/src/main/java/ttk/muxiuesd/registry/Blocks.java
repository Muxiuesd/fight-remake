package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.instance.*;
import ttk.muxiuesd.world.item.instence.BlockItem;

import java.util.function.Supplier;

public final class Blocks {
    public static void init () {
        Log.print(Blocks.class.getName(), "游戏方块注册完毕");
    }

    public static final Block TEST_BLOCK = register("block_test", BlockTest::new);
    public static final Block GRASS = register("grass", BlockGrass::new);
    public static final Block STONE = register("stone", BlockStone::new);
    public static final Block SAND = register("sand", BlockSand::new);
    public static final Block WATER = register("water", BlockWater::new);

    public static final Block CRAFTING_TABLE = register("crafting_table", BlockCraftingTable::new);
    public static final Block FURNACE = register("furnace", BlockFurnace::new);


    public static Block register (String name, Supplier<Block> factory) {
        String id = Fight.getId(name);
        Identifier identifier = new Identifier(id);
        Registries.BLOCK.register(identifier, factory);
        Block block = factory.get().setID(id);
        //注册方块物品
        Items.register(name, () -> new BlockItem(id, id));
        return block;
    }
}

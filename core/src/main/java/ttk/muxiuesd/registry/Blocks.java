package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.instance.*;

import java.util.function.Supplier;

public final class Blocks {
    public static void init () {
    }
    //普通方块
    public static final Block ARI = register("air", BlockAir::new);
    public static final Block TEST_BLOCK = register("block_test", BlockTest::new);
    public static final Block GRASS = register("grass", BlockGrass::new);
    public static final Block STONE = register("stone", BlockStone::new);
    public static final Block SAND = register("sand", BlockSand::new);
    public static final Block WATER = register("water", BlockWater::new);
    public static final Block GLASS = register("glass");
    public static final Block COAL_ORE = register("coal_ore");

    //带有方块实体的方块
    public static final Block CRAFTING_TABLE = register("crafting_table", BlockCraftingTable::new);
    public static final Block FURNACE = register("furnace", BlockFurnace::new);

    /**
     * 注册一个非常普通的方块
     * */
    public static Block register (String name) {
        return register(name, Block.createProperty());
    }

    public static Block register (String name, Block.Property property) {
        return register(name, () -> new CommonBlock(name, property));
    }

    /**
     * 方块注册的基本方法
     * */
    public static Block register (String name, Supplier<Block> factory) {
        String id = Fight.getId(name);
        Identifier identifier = new Identifier(id);
        return Registries.BLOCK.register(identifier, factory.get().setID(id));
    }
}

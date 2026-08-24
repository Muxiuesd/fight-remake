package ttk.muxiuesd.registry;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.loottable.block.BlockDropLootTable;

import java.util.HashMap;

/**
 * 方块的掉落物战利品表注册
 * */
public class BlockDropLootTables {
    public static void init () {
        Log.print(BlockDropLootTables.class.getName(), "方块掉落战利品表注册完成");
    }

    public static final HashMap<Block, BlockDropLootTable> DROP_SELF_GROUP = new HashMap<>();
    static {
        dropSelf(
            //自然方块
            Blocks.GRASS, Blocks.STONE, Blocks.SAND, Blocks.FARMLAND_DRY, Blocks.WATER,

            //建筑方块
            Blocks.GLASS,

            //矿物方块
            Blocks.COAL_ORE,

            //颜色方块
            Blocks.WOOL_BLACK, Blocks.WOOL_BLUE, Blocks.WOOL_BROWN, Blocks.WOOL_CYAN, Blocks.WOOL_GRAY, Blocks.WOOL_GREEN, Blocks.WOOL_LIGHT_BLUE, Blocks.WOOL_LIME, Blocks.WOOL_MAGENTA, Blocks.WOOL_ORANGE, Blocks.WOOL_PINK, Blocks.WOOL_PURPLE, Blocks.WOOL_RED, Blocks.WOOL_SILVER, Blocks.WOOL_WHITE, Blocks.WOOL_YELLOW,

            //有方块实体的方块
            Blocks.CRAFTING_TABLE, Blocks.FURNACE,

            //墙体方块
            Walls.SMOOTH_STONE
        );
    }


    private static void dropSelf (Block... blocks) {
        for (Block block : blocks) {
            DROP_SELF_GROUP.put(block, registerDropSelf(block));
        }
    }

    /**
     * 只掉落自己对应的方块物品的快捷注册
     * */
    public static BlockDropLootTable registerDropSelf (Block block) {
        return register(
            block.getIdentifier(),
            BlockDropLootTable.Builder.create()
                .dropSelf(block)
                .build()
        );
    }

    public static BlockDropLootTable register (String name, BlockDropLootTable blockDropLootTable) {
        return register(Identifier.of(Fight.ID(name)), blockDropLootTable);
    }

    public static BlockDropLootTable register (Identifier identifier, BlockDropLootTable blockDropLootTable) {
        return Registries.BLOCK_DROP_LOOT_TABLE.register(identifier, blockDropLootTable);
    }
}

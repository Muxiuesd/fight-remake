package ttk.muxiuesd.registry;

import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.loottable.block.BlockDropLootTable;
import ttk.muxiuesd.world.loottable.common.LootCondition;
import ttk.muxiuesd.world.loottable.common.LootEntry;
import ttk.muxiuesd.world.loottable.common.LootGroup;

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

        //作物：按成熟度掉不同的东西（成熟度由 Attachment#beDestroyed 传进来）
        register(Blocks.POTATO.getIdentifier(), BlockDropLootTable.Builder.create()
            .setGroups(
                LootGroup.of(LootCondition.IMMATURE, "potato_immature",
                    LootEntry.of(Items.POTATO, 1, 1, 1f)),
                LootGroup.of(LootCondition.MATURE, "potato_mature",
                    LootEntry.of(Items.POTATO, 2, 4, 1f))
            )
            .build()
        );
        register(Blocks.WHEAT.getIdentifier(), BlockDropLootTable.Builder.create()
            .setGroups(
                LootGroup.of(LootCondition.IMMATURE, "wheat_immature",
                    LootEntry.of(Items.SEEDS_WHEAT, 1, 1, 1f)),
                LootGroup.of(LootCondition.MATURE, "wheat_mature",
                    LootEntry.of(Items.WHEAT, 1, 3, 1f)),
                LootGroup.of(LootCondition.MATURE, "wheat_mature_seeds",
                    LootEntry.of(Items.SEEDS_WHEAT, 1, 3, 1f))
            )
            .build()
        );
    }

    /**
     * 按注册的掉落表生成掉落
     * <p>
     * 方块、墙体、附着物共用这一个入口；没有注册掉落表时返回false，由调用方决定兜底怎么掉
     * @param mature 目标是否成熟，只有附着物（植物）用得到，其它方块类一律传false
     * @return 找到了掉落表并已经生成掉落物返回true
     * */
    public static boolean generate (World world, Block block, Vector2 position, boolean mature) {
        BlockDropLootTable lootTable = Registries.BLOCK_DROP_LOOT_TABLE.getOrNull(block.getIdentifier());
        if (lootTable == null) return false;
        lootTable.generate(world, new BlockDropLootTable.Conditions()
            .setPos(position)
            .setMature(mature));
        return true;
    }


    private static void dropSelf (Block... blocks) {
        for (Block block : blocks) {
            DROP_SELF_GROUP.put(block, registerDropSelf(block));
        }
    }

    /**
     * 只掉落自己对应的方块物品的快捷注册（需要对应的方块物品已注册）
     * */
    public static BlockDropLootTable registerDropSelf (Block block) {
        return register(
            block.getIdentifier(),
            BlockDropLootTable.Builder.create()
                .dropSelf(block)
                .build()
        );
    }

    public static BlockDropLootTable register (String name, BlockDropLootTable lootTable) {
        return register(Identifier.of(Fight.ID(name)), lootTable);
    }

    public static BlockDropLootTable register (Identifier identifier, BlockDropLootTable lootTable) {
        return Registries.BLOCK_DROP_LOOT_TABLE.register(identifier, lootTable);
    }
}

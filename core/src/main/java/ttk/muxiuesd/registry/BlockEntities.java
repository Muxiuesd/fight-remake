package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.block.blockentity.BlockEntityCraftingTable;
import ttk.muxiuesd.world.block.blockentity.BlockEntityFurnace;
import ttk.muxiuesd.world.block.blockentity.BlockEntityProvider;

/**
 * 所有方块实体的注册
 * */
public final class BlockEntities {
    public static void init () {}


    public static final BlockEntityProvider<BlockEntityFurnace> FURNACE = register(
        "furnace",
        BlockEntityFurnace::new
    );
    public static final BlockEntityProvider<BlockEntityCraftingTable> CRAFTING_TABLE = register(
        "crafting_table",
        BlockEntityCraftingTable::new
    );

    public static <T extends BlockEntityProvider.Factory<?>> BlockEntityProvider register (String name, T provider) {
        String id = Fight.getId(name);
        BlockEntityProvider blockEntityProvider = new BlockEntityProvider(id, provider);
        return Registries.BLOCK_ENTITY.register(new Identifier(id), blockEntityProvider);
    }
}

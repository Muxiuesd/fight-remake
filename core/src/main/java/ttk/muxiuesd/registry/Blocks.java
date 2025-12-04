package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.render.world.block.BlockEntityRenderer;
import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.registrant.BlockEntityRendererRegistry;
import ttk.muxiuesd.registrant.BlockRendererRegistry;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.block.instance.*;

import java.util.function.Supplier;

/**
 * 游戏内所有的方块的注册
 * */
public final class Blocks {
    public static void init () {}

    /// 普通方块
    public static final BlockAir ARI = register("air", BlockAir::new, BlockAir.RENDERER);
    public static final BlockTest TEST_BLOCK = register("block_test", BlockTest::new);
    public static final BlockGrass GRASS = register("grass", BlockGrass::new);
    public static final BlockStone STONE = register("stone", BlockStone::new);
    public static final BlockSand SAND = register("sand", BlockSand::new);
    public static final BlockWater WATER = register("water", BlockWater::new, BlockWater.RENDERER);
    public static final Block GLASS = register("glass");
    public static final Block COAL_ORE = register("coal_ore");

    /// 带有方块实体的方块
    public static final Block CRAFTING_TABLE = register("crafting_table", BlockCraftingTable::new);
    public static final Block FURNACE = register("furnace", BlockFurnace::new, BlockFurnace.RENDERER);

    /**
     * 注册一个非常普通的方块
     * */
    public static Block register (String name) {
        return register(name, Block.createProperty());
    }

    public static Block register (String name, Block.Property property) {
        return register(name, () -> new CommonBlock(name, property));
    }

    public static <T extends Block> T register (String name, Supplier<T> factory) {
        return register(name, factory, new BlockRenderer.StandardRenderer<>());
    }

    /**
     * 方块注册的基本方法
     * @param name 名字
     * @param factory 工厂类（用于语法糖）
     * @param renderer 渲染器
     * */
    public static <T extends Block> T register (String name, Supplier<T> factory, BlockRenderer<T> renderer) {
        String id = Fight.ID(name);
        Identifier identifier = new Identifier(id);
        T block = factory.get();
        block.setID(id);
        Registries.BLOCK.register(identifier, block);

        registerBlockRenderer(block, renderer);
        if (block instanceof BlockWithEntity blockWithEntity) {
            registerBlockEntityRenderer(blockWithEntity, blockWithEntity.getBlockEntityRenderer());
        }

        return block;
    }

    public static <T extends Block> void registerBlockRenderer (T block, BlockRenderer<T> renderer) {
        BlockRendererRegistry.register(block, renderer);
    }

    public static <T extends BlockWithEntity, E extends BlockEntity>
    void registerBlockEntityRenderer (T blockWithEntity, BlockEntityRenderer<E> renderer) {
        BlockEntityRendererRegistry.register(blockWithEntity, renderer);
    }
}

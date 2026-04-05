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
    //矿
    public static final Block COAL_ORE = register("coal_ore");
    //颜色方块
    public static final Block WOOL_BLACK = register("wool_colored_black", "wool/wool_colored_black");
    public static final Block WOOL_BLUE = register("wool_colored_blue", "wool/wool_colored_blue");
    public static final Block WOOL_BROWN = register("wool_colored_brown", "wool/wool_colored_brown");
    public static final Block WOOL_CYAN = register("wool_colored_cyan", "wool/wool_colored_cyan");
    public static final Block WOOL_GRAY = register("wool_colored_gray", "wool/wool_colored_gray");
    public static final Block WOOL_GREEN = register("wool_colored_green", "wool/wool_colored_green");
    public static final Block WOOL_LIGHT_BLUE = register("wool_colored_light_blue", "wool/wool_colored_light_blue");
    public static final Block WOOL_LIME = register("wool_colored_lime", "wool/wool_colored_lime");
    public static final Block WOOL_MAGENTA = register("wool_colored_magenta", "wool/wool_colored_magenta");
    public static final Block WOOL_ORANGE = register("wool_colored_orange", "wool/wool_colored_orange");
    public static final Block WOOL_PINK = register("wool_colored_pink", "wool/wool_colored_pink");
    public static final Block WOOL_PURPLE = register("wool_colored_purple", "wool/wool_colored_purple");
    public static final Block WOOL_RED = register("wool_colored_red", "wool/wool_colored_red");
    public static final Block WOOL_SILVER = register("wool_colored_silver", "wool/wool_colored_silver");
    public static final Block WOOL_WHITE = register("wool_colored_white", "wool/wool_colored_white");
    public static final Block WOOL_YELLOW = register("wool_colored_yellow", "wool/wool_colored_yellow");

    /// 带有方块实体的方块
    public static final Block CRAFTING_TABLE = register("crafting_table", BlockCraftingTable::new);
    public static final Block FURNACE = register("furnace", BlockFurnace::new);

    /**
     * 注册一个非常普通的方块
     * */
    public static Block register (String name) {
        return register(name, Block.createProperty());
    }

    /**
     * 注册一个名称与贴图路径不完全相同的方块
     * @param pathName 在{@link Fight#BLOCK_TEXTURE_ROOT}下的路径
     * */
    public static Block register (String name, String pathName) {
        return register(name, () -> new CommonBlock(name, pathName, Block.createProperty()));
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

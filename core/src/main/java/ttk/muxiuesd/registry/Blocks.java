package ttk.muxiuesd.registry;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.render.world.block.BlockEntityRenderer;
import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.registrant.BlockEntityRendererRegistry;
import ttk.muxiuesd.registrant.BlockRendererRegistry;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.render.world.block.BotanyRenderer;
import ttk.muxiuesd.render.world.block.FurnaceRenderer;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.block.abs.Botany;
import ttk.muxiuesd.world.block.instance.BlockAir;
import ttk.muxiuesd.world.block.instance.BlockCraftingTable;
import ttk.muxiuesd.world.block.instance.BlockFurnace;
import ttk.muxiuesd.world.block.instance.BlockWater;
import ttk.muxiuesd.world.block.instance.botany.BotanyPotato;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 游戏内所有的方块的注册
 * */
public final class Blocks {
    public static void init () {
        Log.print(Blocks.class.getName(), "所有方块注册完成");
    }

    /// 普通方块
    //自然方块
    public static final Block ARI = register("air", BlockAir::new, BlockAir.RENDERER);
    public static final Block TEST_BLOCK = registerCommon("block_test",
        () -> new Block.Property().setFriction(0.9f)
    );
    public static final Block GRASS = registerCommon("grass",
        () -> new Block.Property().setFriction(0f)
    );
    public static final Block STONE = registerCommon("stone",
        () -> new Block.Property().setFriction(0f)
    );
    public static final Block SAND = registerCommon("sand",
        () -> new Block.Property().setFriction(0.06f)
    );
    public static final Block FARMLAND_DRY = registerCommon("farmland_dry",
        () -> new Block.Property().setFriction(0.1f)
    );
    public static final Block WATER = register("water", BlockWater::new,
        () -> new Block.Property().setFriction(0.9f),
        BlockWater.RENDERER
    );

    //建筑方块
    public static final Block GLASS = registerCommon("glass",
        () -> new Block.Property().setFriction(0.02f)
    );

    //矿物方块
    public static final Block COAL_ORE = registerCommon("coal_ore",
        () -> new Block.Property().setFriction(0.05f)
    );

    //颜色方块
    public static final Block WOOL_BLACK = register("wool_colored_black", "wool/wool_colored_black",
        () -> new Block.Property().setFriction(0.05f)
    );
    public static final Block WOOL_BLUE = register("wool_colored_blue", "wool/wool_colored_blue",
        () -> new Block.Property().setFriction(0.05f)
    );
    public static final Block WOOL_BROWN = register("wool_colored_brown", "wool/wool_colored_brown",
        () -> new Block.Property().setFriction(0.05f)
    );
    public static final Block WOOL_CYAN = register("wool_colored_cyan", "wool/wool_colored_cyan",
        () -> new Block.Property().setFriction(0.05f)
    );
    public static final Block WOOL_GRAY = register("wool_colored_gray", "wool/wool_colored_gray",
        () -> new Block.Property().setFriction(0.05f)
    );
    public static final Block WOOL_GREEN = register("wool_colored_green", "wool/wool_colored_green",
        () -> new Block.Property().setFriction(0.05f)
    );
    public static final Block WOOL_LIGHT_BLUE = register("wool_colored_light_blue", "wool/wool_colored_light_blue",
        () -> new Block.Property().setFriction(0.05f)
    );
    public static final Block WOOL_LIME = register("wool_colored_lime", "wool/wool_colored_lime",
        () -> new Block.Property().setFriction(0.05f)
    );
    public static final Block WOOL_MAGENTA = register("wool_colored_magenta", "wool/wool_colored_magenta",
        () -> new Block.Property().setFriction(0.05f)
    );
    public static final Block WOOL_ORANGE = register("wool_colored_orange", "wool/wool_colored_orange",
        () -> new Block.Property().setFriction(0.05f)
    );
    public static final Block WOOL_PINK = register("wool_colored_pink", "wool/wool_colored_pink",
        () -> new Block.Property().setFriction(0.05f)
    );
    public static final Block WOOL_PURPLE = register("wool_colored_purple", "wool/wool_colored_purple",
        () -> new Block.Property().setFriction(0.05f)
    );
    public static final Block WOOL_RED = register("wool_colored_red", "wool/wool_colored_red",
        () -> new Block.Property().setFriction(0.05f)
    );
    public static final Block WOOL_SILVER = register("wool_colored_silver", "wool/wool_colored_silver",
        () -> new Block.Property().setFriction(0.05f)
    );
    public static final Block WOOL_WHITE = register("wool_colored_white", "wool/wool_colored_white",
        () -> new Block.Property().setFriction(0.05f)
    );
    public static final Block WOOL_YELLOW = register("wool_colored_yellow", "wool/wool_colored_yellow",
        () -> new Block.Property().setFriction(0.05f)
    );

    /// 植物
    public static final Botany POTATO = registerBotany("potato", BotanyPotato::new,
        "potatoes_stage_0.png",
        "potatoes_stage_1.png",
        "potatoes_stage_2.png",
        "potatoes_stage_3.png"
    );

    /// 带有方块实体的方块
    public static final BlockCraftingTable CRAFTING_TABLE = register("crafting_table", BlockCraftingTable::new);
    public static final BlockFurnace FURNACE = register("furnace", BlockFurnace::new, new FurnaceRenderer());



    /**
     * 注册一个植物方块
     * @param growLevelTextureNames 不同生长等级的贴图文件名（位于 botany/crops/ 目录下），按生长等级从小到大
     * */
    public static <T extends Botany> T registerBotany (String name, Supplier<T> factory, String... growLevelTextureNames) {
        return register(
            name,
            factory,
            new BotanyRenderer<T>(growLevelTextureNames)
        );
    }

    /**
     * 快捷方法：注册一个名称与贴图路径相同的普通方块
     * <p>
     * 直接在{@link Fight#BLOCK_TEXTURE_ROOT}中的贴图文件
     * @param propertyProvider 属性的工厂
     * */
    public static Block registerCommon (String name, Supplier<Block.Property> propertyProvider) {
        return register(
            name,
            name,
            propertyProvider
        );
    }

    /**
     * 注册一个名称与贴图路径不完全相同的普通方块
     * @param pathName 以{@link Fight#BLOCK_TEXTURE_ROOT}为起始的路径的贴图文件
     * @param propertyProvider 属性的工厂
     * */
    public static Block register (String name, String pathName, Supplier<Block.Property> propertyProvider) {
        String path = Fight.BlockTexturePath(pathName);
        //没有后缀自动加上
        if (!path.endsWith(".png")) path += ".png";
        return register(
            name,
            path,
            Block::new,
            propertyProvider
        );
    }

    /**
     * 注册一个使用标准渲染器的方块，显式指定贴图路径（与id相绑定）
     * @param texturePath 贴图文件路径
     * */
    public static <T extends Block> T register (String name,
                                                String texturePath,
                                                Function<Block.Property, T> fun,
                                                Supplier<Block.Property> propertyProvider) {
        Identifier identifier = Identifier.of(Fight.ID(name));
        return register(
            identifier,
            () -> fun.apply(propertyProvider.get()),
            new BlockRenderer.StandardRenderer<T>(identifier.getID(), texturePath)
        );
    }



    /**
     * 注册一个使用自定义渲染器的方块
     * @param fun 方块的工厂
     * @param propertyProvider 属性工厂
     * */
    public static <T extends Block> T register (String name,
                                                Function<Block.Property, T> fun,
                                                Supplier<Block.Property> propertyProvider,
                                                BlockRenderer<T> renderer) {
        return register(
            name,
            () -> fun.apply(propertyProvider.get()),
            renderer
        );
    }

    /**
     * 注册一个名称与贴图路径相同的方块，使用标准方块渲染器
     * @param factory 工厂类（已设置方块属性）
     * */
    public static <T extends Block> T register (String name, Supplier<T> factory) {
        Identifier identifier = Identifier.of(Fight.ID(name));
        return register(
            identifier,
            factory,
            new BlockRenderer.StandardRenderer<T>(identifier.getID(), Fight.BlockTexturePath(name + ".png"))
        );
    }


    /**
     * 注册一个使用自定义渲染器的方块
     * @param factory 工厂类（已设置方块属性）
     * */
    public static <T extends Block> T register (String name, Supplier<T> factory, BlockRenderer<T> renderer) {
        return register(
            Identifier.of(Fight.ID(name)),
            factory,
            renderer
        );
    }

    /**
     * 方块注册的最基本方法
     * @param identifier id标识符
     * @param factory 工厂类（已设置方块属性）
     * @param renderer 渲染器，为null时根据贴图路径自动创建标准渲染器
     * */
    public static <T extends Block> T register (Identifier identifier,
                                                Supplier<T> factory,
                                                BlockRenderer<T> renderer) {
        T block = factory.get();
        block.setIdentifier(identifier);
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

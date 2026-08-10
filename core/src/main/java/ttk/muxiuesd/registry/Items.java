package ttk.muxiuesd.registry;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.render.world.item.ItemRenderer;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.registrant.ItemRendererRegistry;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.render.world.item.FishPoleRenderer;
import ttk.muxiuesd.render.world.item.TorchRenderer;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.Botany;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.common.CommonItem;
import ttk.muxiuesd.world.item.common.ItemFishPole;
import ttk.muxiuesd.world.item.common.ItemStick;
import ttk.muxiuesd.world.item.consumption.*;
import ttk.muxiuesd.world.item.equipment.EquipmentDiamondBoots;
import ttk.muxiuesd.world.item.equipment.EquipmentDiamondChestplate;
import ttk.muxiuesd.world.item.equipment.EquipmentDiamondHelmet;
import ttk.muxiuesd.world.item.equipment.EquipmentDiamondLeggings;
import ttk.muxiuesd.world.item.food.ItemFish;
import ttk.muxiuesd.world.item.food.ItemPufferFish;
import ttk.muxiuesd.world.item.weapon.ItemTorch;
import ttk.muxiuesd.world.item.weapon.WeaponDiamondSword;
import ttk.muxiuesd.world.item.weapon.sword.Sword;
import ttk.muxiuesd.world.item.weapon.sword.SwordBuilder;
import ttk.muxiuesd.world.wall.Wall;

import java.util.function.Supplier;

/**
 * 所有的物品注册
 * */
public final class Items {
    public static void init () {
        Log.print(Items.class.getName(), "所有物品注册完成");
    }

    /// 常规物品
    //材料类
    public static final Item STICK = register("stick", ItemStick::new);
    public static final Item SLIME_BALL = register("slime_ball");
    public static final Item IRON_INGOT = register("iron_ingot");
    public static final Item GOLD_INGOT = register("gold_ingot");
    public static final Item COAL = register("coal");

    public static final Item BAIT = register("bait", ItemBait::new);
    //食物类
    public static final Item FISH = register("fish", ItemFish::new);
    public static final Item PUFFER_FISH = register("puffer_fish", ItemPufferFish::new);
    //杂物类
    public static final Item RUBBISH = register("rubbish");

    /// 工具类物品
    public static final Item FISH_POLE = register("fish_pole", ItemFishPole::new, FishPoleRenderer::new);

    /// 武器类的物品
    public static final Item WOOD_SWORD = registerSword("wood_sword",
        SwordBuilder.create().setAttackRange(2.5f).setDamage(1f).setUseSpan(1f).setDuration(50)
    );
    public static final Item STONE_SWORD = registerSword("stone_sword",
        SwordBuilder.create().setAttackRange(2.5f).setDamage(1.5f).setUseSpan(1f).setDuration(100)
    );
    public static final Item IRON_SWORD = registerSword("iron_sword",
        SwordBuilder.create().setAttackRange(3f).setDamage(3.5f).setUseSpan(0.5f).setDuration(345)
    );
    public static final Item GOLD_SWORD = registerSword("gold_sword",
        SwordBuilder.create().setAttackRange(3f).setDamage(4.5f).setUseSpan(0.5f).setDuration(555)
    );
    //远程类武器
    public static final Item TEST_WEAPON = register("diamond_sword", WeaponDiamondSword::new);
    //火把也能用来攻击
    public static final Item TORCH = register("torch", ItemTorch::new, TorchRenderer::new);

    /// 装备物品
    public static final Item DIAMOND_HELMET = register("diamond_helmet", EquipmentDiamondHelmet::new);
    public static final Item DIAMOND_CHESTPLATE = register("diamond_chestplate", EquipmentDiamondChestplate::new);
    public static final Item DIAMOND_LEGGINGS = register("diamond_leggings", EquipmentDiamondLeggings::new);
    public static final Item DIAMOND_BOOTS = register("diamond_boots", EquipmentDiamondBoots::new);

    /// 刷怪蛋物品
    //怪物刷怪蛋
    public static final Item SPAWN_EGG_SLIME = register("spawn_egg_slime", Entities.SLIME);
    //生物刷怪蛋
    public static final Item SPAWN_EGG_PUFFER_FISH = register("spawn_egg_puffer_fish", Entities.PUFFER_FISH);

    /// 方块物品
    public static final Item TEST_BLOCK = register(Blocks.TEST_BLOCK);
    public static final Item GRASS = register(Blocks.GRASS);
    public static final Item FARMLAND_DRY = register(Blocks.FARMLAND_DRY);
    public static final Item STONE = register(Blocks.STONE);
    public static final Item SAND = register(Blocks.SAND);
    public static final Item WATER = register(Blocks.WATER);
    public static final Item GLASS = register(Blocks.GLASS);
    public static final Item COAL_ORE = register(Blocks.COAL_ORE);
    //颜色方块物品
    public static final Item WOOL_BLACK = register(Blocks.WOOL_BLACK);
    public static final Item WOOL_BLUE = register(Blocks.WOOL_BLUE);
    public static final Item WOOL_BROWN = register(Blocks.WOOL_BROWN);
    public static final Item WOOL_CYAN = register(Blocks.WOOL_CYAN);
    public static final Item WOOL_GRAY = register(Blocks.WOOL_GRAY);
    public static final Item WOOL_GREEN = register(Blocks.WOOL_GREEN);
    public static final Item WOOL_LIGHT_BLUE = register(Blocks.WOOL_LIGHT_BLUE);
    public static final Item WOOL_LIME = register(Blocks.WOOL_LIME);
    public static final Item WOOL_MAGENTA = register(Blocks.WOOL_MAGENTA);
    public static final Item WOOL_ORANGE = register(Blocks.WOOL_ORANGE);
    public static final Item WOOL_PINK = register(Blocks.WOOL_PINK);
    public static final Item WOOL_PURPLE = register(Blocks.WOOL_PURPLE);
    public static final Item WOOL_RED = register(Blocks.WOOL_RED);
    public static final Item WOOL_SILVER = register(Blocks.WOOL_SILVER);
    public static final Item WOOL_WHITE = register(Blocks.WOOL_WHITE);
    public static final Item WOOL_YELLOW = register(Blocks.WOOL_YELLOW);

    /// 带有方块实体的方块物品
    public static final Item CRAFTING_TABLE = register(Blocks.CRAFTING_TABLE);
    public static final Item FURNACE = register(Blocks.FURNACE);

    /// 农作物物品
    public static final Item POTATO = register("potato", Blocks.POTATO);

    /// 墙体物品
    public static final Item SMOOTH_STONE = register(Walls.SMOOTH_STONE);


    /**
     * 注册一个剑类物品（快捷方法）
     * */
    public static Sword registerSword (String name, SwordBuilder builder) {
        String id = Fight.ID(name);
        Identifier identifier = Identifier.of(id);
        return register(
            () -> builder.build(id, Fight.ItemTexturePath(name + ".png")),
            new ItemRenderer.StandardRenderer<>(),
            identifier
        );
    }


    /**
     * 注册农作物物品
     * */
    public static CropItem register (String name, Botany crop) {
        CropItem cropItem = register(name, () -> new CropItem(name, crop));
        crop.setDroppedItem(cropItem);
        return cropItem;
    }

    /**
     * 普通物品的注册
     * */
    public static Item register (String name) {
        return register(name, () -> new CommonItem(name));
    }

    /**
     * 刷怪蛋物品的注册
     * */
    public static <T extends Entity<T>> Item register (String name, EntityProvider<T> entityProvider) {
        return register(name, () -> new SpawnEggItem<>(name, entityProvider));
    }

    /**
     * 根据名字来创建id
     * <p>
     * 使用普通标准渲染器
     * */
    public static <T extends Item> T register (String name, Supplier<T> factory) {
        return register(factory, new ItemRenderer.StandardRenderer<>(), Identifier.of(Fight.ID(name)));
    }


    /**
     * 根据名字来创建id
     * <p>
     * 使用自定义的渲染器
     * */
    public static <T extends Item> T register (String name, Supplier<T> factory, Supplier<ItemRenderer<T>> rendererFactory) {
        return register(factory, rendererFactory.get(), Identifier.of(Fight.ID(name)));
    }

    /**
     * 根据名字来创建id
     * <p>
     * 使用自定义的渲染器
     * */
    public static <T extends Item> T register (String name, Supplier<T> factory, ItemRenderer<T> renderer) {
        return register(factory, renderer, Identifier.of(Fight.ID(name)));
    }

    /**
     * 注册方块的方块物品
     * @param block 已经注册过的方块
     * */
    public static Item register (Block block) {
        String id = block.getID();
        return register(
            () -> new BlockItem(block, id), //方块的id默认作为方块物品的贴图材质id
            new ItemRenderer.StandardRenderer<>(),
            block.getIdentifier()   //方块的identifier与方块物品的identifier相同
        );
    }

    /**
     * 注册墙体的墙体物品
     * */
    public static <T extends Wall<T>> Item register (Wall<T> wall) {
        String id = wall.getID();
        //墙体的id默认是墙体物品的贴图材质id
        return register(
            () -> new WallItem(wall, id),
            new ItemRenderer.StandardRenderer<>(),
            wall.getIdentifier()    //墙体的identifier与墙体物品的identifier相同
        );
    }

    /**
     * 物品注册的基本方法
     * @param factory 物品的构造工厂
     * @param renderer 物品的渲染器
     * @param identifier 物品的id标识
     * */
    public static <T extends Item> T register (Supplier<T> factory, ItemRenderer<T> renderer, Identifier identifier) {
        T item = factory.get();
        item.setIdentifier(identifier);
        Registries.ITEM.register(identifier, item);
        ItemRendererRegistry.register(item, renderer);
        return item;
    }
}

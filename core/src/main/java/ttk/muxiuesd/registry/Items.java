package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.render.world.item.ItemRenderer;
import ttk.muxiuesd.registrant.ItemRendererRegistry;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.render.world.item.FishPoleRenderer;
import ttk.muxiuesd.render.world.item.TorchRenderer;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.common.CommonItem;
import ttk.muxiuesd.world.item.common.ItemStick;
import ttk.muxiuesd.world.item.consumption.BlockItem;
import ttk.muxiuesd.world.item.consumption.ItemBait;
import ttk.muxiuesd.world.item.consumption.ItemFishPole;
import ttk.muxiuesd.world.item.consumption.WallItem;
import ttk.muxiuesd.world.item.equipment.EquipmentDiamondBoots;
import ttk.muxiuesd.world.item.equipment.EquipmentDiamondChestplate;
import ttk.muxiuesd.world.item.equipment.EquipmentDiamondHelmet;
import ttk.muxiuesd.world.item.equipment.EquipmentDiamondLeggings;
import ttk.muxiuesd.world.item.food.ItemFish;
import ttk.muxiuesd.world.item.food.ItemPufferFish;
import ttk.muxiuesd.world.item.weapon.IronSword;
import ttk.muxiuesd.world.item.weapon.ItemTorch;
import ttk.muxiuesd.world.item.weapon.WeaponDiamondSword;
import ttk.muxiuesd.world.wall.Wall;

import java.util.function.Supplier;

/**
 * 所有的物品注册
 * */
public final class Items {
    public static void init () {}

    /// 常规物品
    public static final Item STICK = register("stick", ItemStick::new);
    public static final Item FISH_POLE = register("fish_pole", ItemFishPole::new, FishPoleRenderer::new);
    public static final Item BAIT = register("bait", ItemBait::new);
    public static final Item FISH = register("fish", ItemFish::new);
    public static final Item PUFFER_FISH = register("puffer_fish", ItemPufferFish::new);
    public static final Item RUBBISH = register("rubbish");
    public static final Item COAL = register("coal");
    public static final Item SLIME_BALL = register("slime_ball");

    /// 武器类的物品
    public static final Item IRON_SWORD = register("iron_sword", IronSword::new);
    public static final Item TEST_WEAPON = register("diamond_sword", WeaponDiamondSword::new);
    public static final Item TORCH = register("torch", ItemTorch::new, TorchRenderer::new);

    /// 装备物品
    public static final Item DIAMOND_HELMET = register("diamond_helmet", EquipmentDiamondHelmet::new);
    public static final Item DIAMOND_CHESTPLATE = register("diamond_chestplate", EquipmentDiamondChestplate::new);
    public static final Item DIAMOND_LEGGINGS = register("diamond_leggings", EquipmentDiamondLeggings::new);
    public static final Item DIAMOND_BOOTS = register("diamond_boots", EquipmentDiamondBoots::new);

    /// 方块物品
    public static final Item TEST_BLOCK = register(Blocks.TEST_BLOCK);
    public static final Item GRASS = register(Blocks.GRASS);
    public static final Item STONE = register(Blocks.STONE);
    public static final Item SAND = register(Blocks.SAND);
    public static final Item WATER = register(Blocks.WATER);
    public static final Item GLASS = register(Blocks.GLASS);
    public static final Item COAL_ORE = register(Blocks.COAL_ORE);

    /// 带有方块实体的方块物品
    public static final Item CRAFTING_TABLE = register(Blocks.CRAFTING_TABLE);
    public static final Item FURNACE = register(Blocks.FURNACE);

    /// 墙体物品
    public static final Item SMOOTH_STONE = register(Walls.SMOOTH_STONE);



    /**
     * 普通物品的注册
     * */
    public static Item register (String name) {
        return register(name, () -> new CommonItem(name));
    }

    /**
     * 根据名字来创建id
     * <p>
     * 使用普通标准渲染器
     * */
    public static <T extends Item> T register (String name, Supplier<T> factory) {
        return register(factory, new ItemRenderer.StandardRenderer<>(), Fight.ID(name));
    }


    /**
     * 根据名字来创建id
     * <p>
     * 使用自定义的渲染器
     * */
    public static <T extends Item> T register (String name, Supplier<T> factory, Supplier<ItemRenderer<T>> rendererFactory) {
        return register(factory, rendererFactory.get(), Fight.ID(name));
    }

    /**
     * 根据名字来创建id
     * <p>
     * 使用自定义的渲染器
     * */
    public static <T extends Item> T register (String name, Supplier<T> factory, ItemRenderer<T> renderer) {
        return register(factory, renderer, Fight.ID(name));
    }

    /**
     * 注册方块的方块物品
     * */
    public static Item register (Block block) {
        String id = block.getID();
        return register(() -> new BlockItem(block, id), new ItemRenderer.StandardRenderer<>(), id);
    }

    /**
     * 注册墙体的墙体物品
     * */
    public static <T extends Wall<T>> Item register (Wall<T> wall) {
        String id = wall.getID();
        return register(() -> new WallItem(wall, id), new ItemRenderer.StandardRenderer<>(), id);
    }

    /**
     * 物品注册的基本方法
     *
     * @param factory 物品的构造工厂
     * @param renderer 物品的渲染器
     * @param id 物品的id
     * */
    public static <T extends Item> T register (Supplier<T> factory, ItemRenderer<T> renderer, String id) {
        Identifier identifier = new Identifier(id);
        T item = factory.get();
        item.setID(id);
        Registries.ITEM.register(identifier, item);
        ItemRendererRegistry.register(item, renderer);
        return item;
    }
}

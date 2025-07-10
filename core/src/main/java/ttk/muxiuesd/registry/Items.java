package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.instence.*;

import java.util.function.Supplier;

/**
 * 所有的物品
 * */
public final class Items {
    public static void init () {
    }

    //常规物品
    public static final Item TEST_ITEM = register("test_item", IronSword::new);
    public static final Item STICK = register("stick", ItemStick::new);
    public static final Item TEST_WEAPON = register("test_weapon", WeaponDiamondSword::new);
    public static final Item FISH_POLE = register("fish_pole", ItemFishPole::new);
    public static final Item BAIT = register("bait", ItemBait::new);
    public static final Item FISH = register("fish", ItemFish::new);
    public static final Item RUBBISH = register("rubbish");
    public static final Item COAL = register("coal");

    //方块物品
    public static final Item TEST_BLOCK = register(Blocks.TEST_BLOCK);
    public static final Item GRASS = register(Blocks.GRASS);
    public static final Item STONE = register(Blocks.STONE);
    public static final Item SAND = register(Blocks.SAND);
    public static final Item WATER = register(Blocks.WATER);
    public static final Item GLASS = register(Blocks.GLASS);
    public static final Item COAL_ORE = register(Blocks.COAL_ORE);

    //带有方块实体的方块物品
    public static final Item CRAFTING_TABLE = register(Blocks.CRAFTING_TABLE);
    public static final Item FURNACE = register(Blocks.FURNACE);

    /**
     * 普通物品的注册
     * */
    public static Item register (String name) {
        return register(name, () -> new CommonItem(name));
    }

    /**
     * 物品注册的基本方法
     * */
    public static Item register (String name, Supplier<Item> factory) {
        String id = Fight.getId(name);
        Identifier identifier = new Identifier(id);
        return Registries.ITEM.register(identifier, factory.get()).setID(id);
    }

    /**
     * 注册方块的方块物品
     * */
    public static Item register (Block block) {
        String id = block.getID();
        return Registries.ITEM.register(new Identifier(id), new BlockItem(block, id));
    }
}

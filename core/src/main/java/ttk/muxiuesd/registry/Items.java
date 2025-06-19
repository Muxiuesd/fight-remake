package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.instence.*;

import java.util.function.Supplier;

public final class Items {
    public static void init () {
        Log.print(Items.class.getName(), "物品注册完成");
    }

    public static final Item TEST_ITEM = register("test_item", TestItem::new);
    public static final Item STICK = register("stick", ItemStick::new);
    public static final Item TEST_WEAPON = register("test_weapon", WeaponTest::new);
    public static final Item FISH_POLE = register("fish_pole", ItemFishPole::new);
    public static final Item BAIT = register("bait", ItemBait::new);
    public static final Item FISH = register("fish", ItemFish::new);
    public static final Item RUBBISH = register("rubbish", ItemRubbish::new);

    public static final Item TEST_BLOCK = register(Blocks.TEST_BLOCK);
    public static final Item GRASS = register(Blocks.GRASS);
    public static final Item STONE = register(Blocks.STONE);
    public static final Item SAND = register(Blocks.SAND);
    public static final Item WATER = register(Blocks.WATER);

    public static final Item CRAFTING_TABLE = register(Blocks.CRAFTING_TABLE);
    public static final Item FURNACE = register(Blocks.FURNACE);

    public static Item register(String name, Supplier<Item> factory) {
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

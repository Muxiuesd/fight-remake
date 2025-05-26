package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.instence.*;

import java.util.function.Supplier;

public class Items {
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


    public static Item register(String name, Supplier<Item> factory) {
        String id = Fight.getId(name);
        Identifier identifier = new Identifier(id);
        Registries.ITEM.register(identifier, factory);
        Item item = factory.get();
        return item.setID(id);
    }
}

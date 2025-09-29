package ttk.muxiuesd.world.item;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.common.ItemRubbish;
import ttk.muxiuesd.world.item.common.ItemStick;
import ttk.muxiuesd.world.item.consumption.ItemBait;
import ttk.muxiuesd.world.item.consumption.ItemFishPole;
import ttk.muxiuesd.world.item.food.ItemFish;
import ttk.muxiuesd.world.item.weapon.IronSword;
import ttk.muxiuesd.world.item.weapon.WeaponDiamondSword;

import java.util.function.Supplier;

/**
 * 所有物品的注册
 * */
public class ItemsReg {
    public static final Registrant<Item> registrant = RegistrantGroup.getRegistrant(Fight.NAMESPACE, Item.class);

    public static final Item TEST_ITEM = register("test_item", IronSword::new);
    public static final Item STICK = register("stick", ItemStick::new);
    public static final Item TEST_WEAPON = register("test_weapon", WeaponDiamondSword::new);
    public static final Item FISH_POLE = register("fish_pole", ItemFishPole::new);
    public static final Item BAIT = register("bait", ItemBait::new);
    public static final Item FISH = register("fish", ItemFish::new);
    public static final Item RUBBISH = register("rubbish", ItemRubbish::new);


    public static void registerAllItem () {
        Log.print(ItemsReg.class.getName(), "物品注册完成");
    }

    public static Item register (String name, Supplier<? extends Item> supplier) {
        return registrant.register(name, supplier);
    }

    public static ItemStack getItem (String name) {
        return new ItemStack(registrant.get(name));
    }

    public static ItemStack getItem (String name, int amount) {
        return new ItemStack(registrant.get(name), amount);
    }
}

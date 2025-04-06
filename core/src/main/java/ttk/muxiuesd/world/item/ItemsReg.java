package ttk.muxiuesd.world.item;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.IItemStackBehaviour;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.instence.ItemStick;
import ttk.muxiuesd.world.item.instence.TestItem;
import ttk.muxiuesd.world.item.instence.WeaponTest;

import java.util.function.Supplier;

/**
 * 所有物品的注册
 * */
public class ItemsReg {
    public static final Registrant<Item> registrant = RegistrantGroup.getRegistrant(Fight.NAMESPACE, Item.class);

    public static final Item TEST_ITEM = register("test_item", TestItem::new);
    public static final Item STICK = register("stick", ItemStick::new);
    public static final Item TEST_WEAPON = register("test_weapon", WeaponTest::new);

    public static Item register (String name, Supplier<? extends Item> supplier) {
        return registrant.register(name, supplier);
    }

    public static ItemStack getItem (String name) {
        return new ItemStack(registrant.get(name));
    }

    public static ItemStack getItem (String name, int amount) {
        return new ItemStack(registrant.get(name), amount);
    }

    public static ItemStack getItem (String name, int amount, IItemStackBehaviour behaviour) {
        return new ItemStack(registrant.get(name), amount, behaviour);
    }
}

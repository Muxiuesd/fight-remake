package ttk.muxiuesd.world.item;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.instence.ItemStick;
import ttk.muxiuesd.world.item.instence.TestItem;

import java.util.function.Supplier;

/**
 * 所有物品的注册
 * */
public class ItemsReg {
    public static final Registrant<Item> registrant = RegistrantGroup.getRegistrant(Fight.NAMESPACE, Item.class);

    public static final Item TEST_ITEM = register("test_item", TestItem::new);
    public static final Item STICK = register("stick", ItemStick::new);


    public static Item register (String name, Supplier<Item> supplier) {
        return registrant.register(name, supplier);
    }

    public static ItemStack getItem (String name) {
        return new ItemStack(registrant.get(name));
    }
}

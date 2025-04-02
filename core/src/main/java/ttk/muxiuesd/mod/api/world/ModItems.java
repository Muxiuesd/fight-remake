package ttk.muxiuesd.mod.api.world;

import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * modAPI：模组物品相关
 * */
public class ModItems {
    public static ItemStack getItem (String id) {
        String[] parts = id.split(":");
        Registrant<Item> otherReg = RegistrantGroup.getRegistrant(parts[0], Item.class);
        return new ItemStack(otherReg.get(parts[1]));
    }
}

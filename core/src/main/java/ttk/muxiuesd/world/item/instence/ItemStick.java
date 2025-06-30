package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 棍子
 * */
public class ItemStick extends Item {
    public ItemStick () {
        super(Type.CONSUMPTION, new Property().setMaxCount(16),
            Fight.getId("stick"),
            Fight.ItemTexturePath("stick.png"));
    }
}

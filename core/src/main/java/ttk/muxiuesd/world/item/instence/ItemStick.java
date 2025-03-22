package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 棍子
 * */
public class ItemStick extends Item {
    public ItemStick () {
        super(new Property().setMaxCount(64),
            Fight.getId("stick"),
            Fight.getItemTexture("stick.png"));
    }
}

package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 垃圾物品
 * */
public class ItemRubbish extends Item {
    public ItemRubbish () {
        super(Type.COMMON, new Property().setMaxCount(64),
            Fight.getId("rubbish"),
            Fight.ItemTexturePath("rubbish.png"));
    }
}

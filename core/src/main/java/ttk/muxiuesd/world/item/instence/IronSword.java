package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.Fight;

/**
 * 铁剑
 * */
public class IronSword extends Sword {
    public IronSword () {
        super(Sword.createDefaultProperty(),
            Fight.getId("test_item"),
            Fight.ItemTexturePath("iron_sword.png"));
    }
}

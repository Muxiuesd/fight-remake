package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.item.abs.Item;

public class TestItem extends Item {
    public TestItem () {
        super(Type.COMMON, new Property().setMaxCount(1),
            Fight.getId("test_item"),
            Fight.ItemTexturePath("iron_sword.png"));
    }
}

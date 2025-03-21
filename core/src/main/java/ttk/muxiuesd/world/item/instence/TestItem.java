package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.item.abs.Item;

public class TestItem extends Item {
    public TestItem () {
        super(new Property().setMaxCount(32),
            Fight.getId("test_item"),
            Fight.getItemTexture("iron_sword.png"));
    }
}

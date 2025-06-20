package ttk.muxiuesd.interfaces.world.item;

import ttk.muxiuesd.world.item.ItemStack;

public interface ItemUpdateable {
    void update(float delta, ItemStack itemStack);
}

package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.Fight;

/**
 * 物品：鱼
 * */
public class ItemFish extends FoodItem {
    public ItemFish () {
        super(Fight.getId("fish"), Fight.ItemTexturePath("fish.png"));
    }
}

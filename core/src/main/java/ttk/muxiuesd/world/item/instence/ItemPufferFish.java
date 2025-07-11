package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.Fight;

/**
 * 物品河豚
 * */
public class ItemPufferFish extends FoodItem{
    public ItemPufferFish () {
        super(Fight.getId("puffer_fish"), Fight.EntityTexturePath("fish/puffer_fish.png"));
    }
}

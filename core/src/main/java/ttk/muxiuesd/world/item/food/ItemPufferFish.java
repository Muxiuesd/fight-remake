package ttk.muxiuesd.world.item.food;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.StatusEffects;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品河豚
 * */
public class ItemPufferFish extends FoodItem {
    public ItemPufferFish () {
        super(Fight.ID("puffer_fish"), Fight.EntityTexturePath("fish/puffer_fish.png"));
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        //吃河豚得中毒效果
        user.setEffect(StatusEffects.POISON, 5, 1);
        return super.use(itemStack, world, user);
    }
}

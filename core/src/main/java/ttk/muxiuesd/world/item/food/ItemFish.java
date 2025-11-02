package ttk.muxiuesd.world.item.food;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.StatusEffects;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品：鱼
 * */
public class ItemFish extends FoodItem {
    public ItemFish () {
        super(Fight.ID("fish"), Fight.ItemTexturePath("fish.png"));
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {

        user.setEffect(StatusEffects.HEALING, 2f, 1);
        return super.use(itemStack, world, user);
    }
}

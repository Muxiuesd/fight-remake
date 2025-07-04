package ttk.muxiuesd.world.item.stack.behaviour;

import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.abs.Weapon;

/**
 * 武器物品使用行为
 * */
public class WeaponItemStackBehaviour implements IItemStackBehaviour {
    @Override
    public boolean use (World world, LivingEntity user, ItemStack itemStack) {
        Weapon weapon = (Weapon) itemStack.getItem();
        Item.Property property = weapon.getProperty();
        if (!itemStack.isReady()) {
            //使用CD未冷却完
            return false;
        }

        boolean used = weapon.use(itemStack, world, user);
        if (!used) {
            return false;
        }
        //用一次耐久减一
        property.setDuration(property.getDuration() - 1);
        //武器使用挥手
        user.swingHand(itemStack.useTimer.getMaxSpan());
        return true;
    }
}

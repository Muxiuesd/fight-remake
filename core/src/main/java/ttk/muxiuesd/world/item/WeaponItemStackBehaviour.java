package ttk.muxiuesd.world.item;

import ttk.muxiuesd.interfaces.IItemStackBehaviour;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.abs.Weapon;

/**
 * 武器物品使用行为
 * */
public class WeaponItemStackBehaviour implements IItemStackBehaviour {
    @Override
    public boolean use (World world, LivingEntity user, ItemStack itemStack) {
        Weapon weapon = (Weapon) itemStack.getItem();
        Weapon.WeaponProperties property = weapon.getProperties();
        if (itemStack.getUseSpan() <= property.getUseSpan()) {
            //使用CD未冷却完
            return false;
        }

        boolean used = weapon.use(world, user);
        if (!used) {
            return false;
        }
        Weapon.WeaponProperties properties = (Weapon.WeaponProperties) itemStack.getProperty();
        //用一次耐久减一
        properties.setDuration(properties.getDuration() - 1);
        itemStack.setUseSpan(0f);
        return true;
    }
}

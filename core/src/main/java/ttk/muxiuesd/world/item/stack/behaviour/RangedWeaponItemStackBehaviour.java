package ttk.muxiuesd.world.item.stack.behaviour;

import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.weapon.RangedWeapon;

/**
 * 远程武器的使用行为
 * */
public class RangedWeaponItemStackBehaviour implements IItemStackBehaviour {
    @Override
    public boolean use (World world, LivingEntity<?> user, ItemStack itemStack) {
        RangedWeapon weapon = (RangedWeapon) itemStack.getItem();
        //获取物品堆叠的属性
        Item.Property property = itemStack.getProperty();
        if (!itemStack.isReady()) {
            //使用CD未冷却完
            return false;
        }

        boolean used = weapon.use(itemStack, world, user);
        if (!used) {
            return false;
        }
        //用成功一次耐久减一
        property.setDuration(property.getDuration() - 1);

        return true;
    }
}

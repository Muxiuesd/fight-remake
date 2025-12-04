package ttk.muxiuesd.world.item.stack.behaviour;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 远程武器的使用行为
 * */
public class RangedWeaponItemStackBehaviour extends HasDurationItemStackBehaviour {
    @Override
    public boolean use (World world, LivingEntity<?> user, ItemStack itemStack) {
        return super.handle(world, user, itemStack);
    }

    @Override
    public boolean hasDuration (World world, LivingEntity<?> user, ItemStack itemStack) {
        //大于0就执行这些操作
        if (!itemStack.isReady()) {
            //使用CD未冷却完
            return false;
        }
        boolean used = itemStack.getItem().use(itemStack, world, user);
        if (!used) {
            return false;
        }

        return super.hasDuration(world, user, itemStack);
    }

    @Override
    public boolean noDuration (World world, LivingEntity<?> user, ItemStack itemStack) {
        //到这就是耐久度归零了
        return super.noDuration(world, user, itemStack);
    }
}

package ttk.muxiuesd.world.item.stack.behaviour;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 武器物品使用行为
 * */
public class SwordItemStackBehaviour extends HasDurationItemStackBehaviour {
    @Override
    public boolean use (World world, LivingEntity<?> user, ItemStack itemStack) {
        return super.handle(world, user, itemStack);
    }

    @Override
    public boolean hasDuration (World world, LivingEntity<?> user, ItemStack itemStack) {
        if (!itemStack.isReady()) {
            //使用CD未冷却完就提前返回
            return false;
        }
        boolean used = itemStack.getItem().use(itemStack, world, user);
        if (!used) {
            //使用不成功提前返回
            return false;
        }
        //武器使用挥手
        user.swingHand(itemStack.useTimer.getMaxSpan());
        //耐久减一，返回使用成功
        return super.hasDuration(world, user, itemStack);
    }

    @Override
    public boolean noDuration (World world, LivingEntity<?> user, ItemStack itemStack) {
        //到这就是耐久度归零了
        return super.noDuration(world, user, itemStack);
    }
}

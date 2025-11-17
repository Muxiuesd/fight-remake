package ttk.muxiuesd.world.item.stack.behaviour;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 普通物品使用行为
 * */
public class CommonItemStackBehaviour extends HasDurationItemStackBehaviour {
    @Override
    public boolean use (World world, LivingEntity<?> user, ItemStack itemStack) {
        return super.handle(world, user, itemStack);
    }

    @Override
    public boolean hasDuration (World world, LivingEntity<?> user, ItemStack itemStack) {
        //有耐久直接使用
        itemStack.getItem().use(itemStack, world, user);
        return super.hasDuration(world, user, itemStack);
    }

    @Override
    public boolean noDuration (World world, LivingEntity<?> user, ItemStack itemStack) {
        return super.noDuration(world, user, itemStack);
    }
}

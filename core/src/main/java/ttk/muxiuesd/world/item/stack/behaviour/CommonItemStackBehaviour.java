package ttk.muxiuesd.world.item.stack.behaviour;

import ttk.muxiuesd.interfaces.IItemStackBehaviour;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 普通物品使用行为
 * */
public class CommonItemStackBehaviour implements IItemStackBehaviour {
    @Override
    public boolean use (World world, LivingEntity user, ItemStack itemStack) {
        return itemStack.getItem().use(world, user);
    }
}

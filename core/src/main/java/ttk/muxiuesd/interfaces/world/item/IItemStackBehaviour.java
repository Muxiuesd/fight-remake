package ttk.muxiuesd.interfaces.world.item;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品堆栈行为逻辑
 * */
public interface IItemStackBehaviour {
    boolean use (World world, LivingEntity user, ItemStack itemStack);
}

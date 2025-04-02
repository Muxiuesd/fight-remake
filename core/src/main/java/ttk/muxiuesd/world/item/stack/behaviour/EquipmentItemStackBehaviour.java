package ttk.muxiuesd.world.item.stack.behaviour;

import ttk.muxiuesd.interfaces.IItemStackBehaviour;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 装备物品使用行为
 * */
public class EquipmentItemStackBehaviour implements IItemStackBehaviour {
    @Override
    public boolean use (World world, LivingEntity user, ItemStack itemStack) {
        return false;
    }
}

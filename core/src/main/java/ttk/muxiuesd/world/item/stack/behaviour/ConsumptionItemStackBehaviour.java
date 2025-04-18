package ttk.muxiuesd.world.item.stack.behaviour;

import ttk.muxiuesd.interfaces.IItemStackBehaviour;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

public class ConsumptionItemStackBehaviour implements IItemStackBehaviour {
    @Override
    public boolean use (World world, LivingEntity user, ItemStack itemStack) {
        boolean used = itemStack.getItem().use(world, user);
        if (!used) {
            return false;
        }
        int count = itemStack.getAmount() - 1;
        if (count > 0) {
            //用一次数量减一
            itemStack.setAmount(count);
        }else {
            //数量用光了
            user.backpack.clear(itemStack);
        }
        return true;
    }
}

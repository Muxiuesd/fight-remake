package ttk.muxiuesd.world.item.stack.behaviour;

import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 消费物品使用行为
 * */
public class ConsumptionItemStackBehaviour implements IItemStackBehaviour {
    @Override
    public boolean use (World world, LivingEntity<?> user, ItemStack itemStack) {
        boolean used = itemStack.getItem().use(itemStack, world, user);
        if (!used) {
            //使用不成功提前返回
            return false;
        }
        //用一次数量减一
        int count = itemStack.getAmount() - 1;
        if (count > 0) {
            itemStack.setAmount(count);
        }else {
            //数量用光了
            user.getBackpack().clear(itemStack);
        }
        return true;
    }
}

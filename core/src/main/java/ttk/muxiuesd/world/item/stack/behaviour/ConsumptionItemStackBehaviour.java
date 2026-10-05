package ttk.muxiuesd.world.item.stack.behaviour;

import com.badlogic.gdx.math.Vector2;
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
        this.consume(user, itemStack);
        return true;
    }

    /**
     * 手持物品对着目标使用，成功才消耗
     * <p>
     * 和{@link #use}同一套语义：物品自己的逻辑返回true才算用掉了这一个
     * */
    @Override
    public boolean useOn (World world, LivingEntity<?> user, ItemStack itemStack, Vector2 targetPos) {
        boolean used = itemStack.getItem().useOn(itemStack, world, user, targetPos);
        if (!used) {
            //使用不成功提前返回
            return false;
        }
        this.consume(user, itemStack);
        return true;
    }

    /**
     * 消耗一个物品
     * */
    private void consume (LivingEntity<?> user, ItemStack itemStack) {
        //用一次数量减一
        int count = itemStack.getAmount() - 1;
        if (count > 0) {
            itemStack.setAmount(count);
        }else {
            //数量用光了
            user.getBackpack().clear(itemStack);
        }
    }
}

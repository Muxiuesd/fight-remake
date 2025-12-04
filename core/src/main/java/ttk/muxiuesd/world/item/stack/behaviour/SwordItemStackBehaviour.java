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
        /*Weapon weapon = (Weapon) itemStack.getItem();
        //获取物品堆叠的属性
        Item.Property property = itemStack.getProperty();
        //检查耐久度是否大于0
        if (property.getDuration() > 0) {
            //大于0就执行这些操作
            if (!itemStack.isReady()) {
                //使用CD未冷却完
                return false;
            }

            boolean used = weapon.use(itemStack, world, user);
            if (!used) {
                return false;
            }
            //用一次耐久减一
            itemStack.durationDecrease(1);
            //武器使用挥手
            user.swingHand(itemStack.useTimer.getMaxSpan());
            //返回使用成功
            return true;
        }

        //到这就是耐久度归零了
        //播放破损音效
        world.getSystem(SoundEffectSystem.class).newSpatialSound(Sounds.ITEM_BREAK, user);

        //使用失败
        return false;*/
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

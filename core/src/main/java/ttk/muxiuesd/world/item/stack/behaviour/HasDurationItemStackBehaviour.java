package ttk.muxiuesd.world.item.stack.behaviour;

import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 对于可能有耐久属性的物品堆叠的行为类
 * */
public abstract class HasDurationItemStackBehaviour implements IItemStackBehaviour {
    /**
     * 对于两种情况的处理
     * */
    public boolean handle (World world, LivingEntity<?> user, ItemStack itemStack) {
        if (this.checkDuration(itemStack)) {
            return hasDuration(world, user, itemStack);
        }

        return noDuration(world, user, itemStack);
    }

    /**
     * 如果有耐久执行的方法，默认耐久减一
     * */
    public boolean hasDuration (World world, LivingEntity<?> user, ItemStack itemStack) {
        //用一次耐久减一
        itemStack.durationDecrease(1);
        return true;
    }

    /**
     * 如果没有耐久执行的方法，默认会播放破损音效
     * */
    public boolean noDuration (World world, LivingEntity<?> user, ItemStack itemStack) {
        //播放破损音效
        world.getSystem(SoundEffectSystem.class).newSpatialSound(Sounds.ITEM_BREAK, user);
        return false;
    }



    /**
     * 检查物品耐久度
     * */
    public boolean checkDuration(ItemStack itemStack) {
        Item.Property stackProperty = itemStack.getProperty();
        //没有耐久属性或者耐久为0都返回false
        return stackProperty.contain(PropertyTypes.ITEM_DURATION)
            && stackProperty.getDuration() > 0;
    }

}

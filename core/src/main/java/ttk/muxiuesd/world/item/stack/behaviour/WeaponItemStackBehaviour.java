package ttk.muxiuesd.world.item.stack.behaviour;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 武器（近战 / 远程）使用行为
 * <p>
 * 合并自原先重复的 {@code SwordItemStackBehaviour}/{@code RangedWeaponItemStackBehaviour}，
 * 通过 {@code swingOnUse} 区分是否使用后挥手。
 */
public class WeaponItemStackBehaviour extends HasDurationItemStackBehaviour {
    private final boolean swingOnUse;   //使用后是否挥手（近战 true，远程 false）

    public WeaponItemStackBehaviour (boolean swingOnUse) {
        this.swingOnUse = swingOnUse;
    }

    @Override
    public boolean hasDuration (World world, LivingEntity<?> user, ItemStack itemStack) {
        if (!itemStack.isReady()) {
            //使用CD未冷却完
            return false;
        }
        boolean used = itemStack.getItem().use(itemStack, world, user);
        if (!used) {
            return false;
        }
        if (this.swingOnUse) {
            //武器使用挥手
            user.swingHand(itemStack.useTimer.getMaxSpan());
        }
        //耐久减一，返回使用成功
        return super.hasDuration(world, user, itemStack);
    }
}
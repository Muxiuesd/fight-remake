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
        if (this.swingOnUse) {
            //近战：攻击动作（挥手）无论是否命中都表现（空挥也挥手，手感保留）；
            //只有命中（use 返回 true）才扣耐久并标记使用中，见下方 used 判断
            user.swingHand(itemStack.getUseTimer().getMaxSpan());
        }
        if (!used) {
            //近战空挥（未命中）/远程发射失败：不标记使用中、不扣耐久
            return false;
        }
        //命中（近战）或发射成功（远程）：标记为使用中（ITEM_ON_USING=true，CD 结束后由 ItemStack.update 自动复位 false）
        itemStack.setOnUsing(true);
        //扣耐久
        return super.hasDuration(world, user, itemStack);
    }
}
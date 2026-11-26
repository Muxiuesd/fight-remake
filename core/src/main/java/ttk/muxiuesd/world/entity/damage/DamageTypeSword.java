package ttk.muxiuesd.world.entity.damage;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.world.entity.abs.DamageType;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;


/**
 * 伤害类型：剑造成的伤害
 * */
public class DamageTypeSword extends DamageType<LivingEntity<?>, LivingEntity<?>> {
    /// 武器默认冲击力（与 Weapon.createDefaultProperty 的默认值一致）
    private static final float DEFAULT_KNOCKBACK = 2f;

    public DamageTypeSword (Identifier identifier) {
        super(identifier);
    }

    @Override
    public void apply (LivingEntity<?> source, LivingEntity<?> target) {
        Float damage = source.getHandItemStack().getProperty().get(PropertyTypes.WEAPON_DAMAGE);

        /// 最终伤害计算公式：
        float finalDamage = Math.max((damage - computeDamageReduction(source, target)), 0.1f);
        target.decreaseHealth(finalDamage);

        Log.print(this.getClass().getName(), "对：" + target + "造成伤害：" + finalDamage);
    }

    /**
     * 击退力度来自攻击者手持武器的冲击力属性
     * <p>
     * 属性缺失（旧存档的物品属性序列化后没有新增的冲击力字段）时
     * 回退到武器默认冲击力，保证击退效果始终生效
     */
    @Override
    public float computeKnockback (LivingEntity<?> source) {
        ItemStack handItemStack = source.getHandItemStack();
        //空手无击退
        if (handItemStack.isVoid()) return 0f;
        Float knockback = handItemStack.getProperty().get(PropertyTypes.WEAPON_KNOCKBACK);
        return knockback != null ? knockback : DEFAULT_KNOCKBACK;
    }
}

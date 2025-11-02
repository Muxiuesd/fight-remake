package ttk.muxiuesd.world.entity.damage;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.entity.abs.DamageType;
import ttk.muxiuesd.world.entity.abs.LivingEntity;


/**
 * 伤害类型：剑造成的伤害
 * */
public class DamageTypeSword extends DamageType<LivingEntity<?>, LivingEntity<?>> {

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
}

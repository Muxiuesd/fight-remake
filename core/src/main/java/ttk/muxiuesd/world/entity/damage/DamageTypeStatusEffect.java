package ttk.muxiuesd.world.entity.damage;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.world.entity.abs.DamageType;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.abs.StatusEffect;

/**
 * 状态效果造成的伤害
 * */
public class DamageTypeStatusEffect extends DamageType<StatusEffect.DamageSource, LivingEntity<?>> {
    public DamageTypeStatusEffect (Identifier identifier) {
        super(identifier);
    }

    @Override
    public void apply (StatusEffect.DamageSource source, LivingEntity<?> target) {
        target.decreaseHealth(source.getDamage(target));
    }
}

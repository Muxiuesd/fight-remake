package ttk.muxiuesd.world.entity.damage;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

public class DamageTypeSword extends DamageType<LivingEntity, LivingEntity> {

    public DamageTypeSword (Identifier identifier) {
        super(identifier);
    }

    @Override
    public void apply (LivingEntity source, LivingEntity target) {
        Float damage = source.getHandItemStack().getProperty().get(PropertyTypes.WEAPON_DAMAGE);
        target.decreaseHealth(damage);
    }
}

package ttk.muxiuesd.world.entity.damage;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.DamageType;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

/**
 * 子弹造成的伤害
 * */
public class DamageTypeBullet extends DamageType<Bullet, LivingEntity<?>> {

    public DamageTypeBullet (Identifier identifier) {
        super(identifier);
    }

    @Override
    public void apply (Bullet source, LivingEntity<?> target) {
        float finalDamage = Math.max((source.getDamage() - computeDamageReduction(source, target)), 0.1f);
        target.decreaseHealth(finalDamage);

        Log.print(this.getClass().getName(), "对：" + target + "造成伤害：" + finalDamage);
    }
}

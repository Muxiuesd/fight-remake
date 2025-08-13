package ttk.muxiuesd.world.entity.damage;

import ttk.muxiuesd.id.Identifier;
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
        target.decreaseHealth(source.getDamage());
        System.out.println(target + " 扣血： " + source.getDamage());
    }
}

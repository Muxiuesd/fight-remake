package ttk.muxiuesd.world.entity.damage;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.world.entity.abs.DamageType;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

/**
 * 伤害类型：近战攻击造成的伤害
 * <p>
 * 伤害值来自攻击者（敌人）自身的近战伤害字段
 * */
public class DamageTypeMelee extends DamageType<Enemy<?>, LivingEntity<?>> {

    public DamageTypeMelee (Identifier identifier) {
        super(identifier);
    }

    @Override
    public void apply (Enemy<?> source, LivingEntity<?> target) {
        float damage = source.getMeleeDamage();

        /// 最终伤害计算公式：
        float finalDamage = Math.max((damage - computeDamageReduction(source, target)), 0.1f);
        target.decreaseHealth(finalDamage);

        Log.print(this.getClass().getName(), "对：" + target + "造成伤害：" + finalDamage);
    }

    /**
     * 击退力度来自攻击者的近战击退字段
     * */
    @Override
    public float computeKnockback (Enemy<?> source) {
        return source.getMeleeKnockback();
    }
}

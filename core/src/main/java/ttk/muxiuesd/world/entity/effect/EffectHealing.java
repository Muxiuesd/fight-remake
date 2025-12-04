package ttk.muxiuesd.world.entity.effect;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.DamageTypes;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.abs.StatusEffect;

/**
 * 治疗效果
 * */
public class EffectHealing extends StatusEffect{
    public EffectHealing (String id) {
        super(id, Fight.UITexturePath("effect/healing.png"));
    }

    @Override
    public void applyEffectTick (LivingEntity<?> entity, int level) {
    }

    @Override
    public void applyEffectPreSecond (LivingEntity<?> entity, int level) {
        float value = 2 * level;
        if (entity instanceof Player) {
            entity.increaseHealth(value);
        }else if (entity instanceof Enemy<?> enemy) {
            //对敌对实体来说，治疗就是伤害
            enemy.applyDamage(DamageTypes.STATUS_EFFECT, new DamageSource(level) {
                @Override
                public float getDamage (LivingEntity<?> victim) {
                    return 3 * getLevel();
                }
            });
        }
    }
}

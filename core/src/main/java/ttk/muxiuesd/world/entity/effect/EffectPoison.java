package ttk.muxiuesd.world.entity.effect;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.DamageTypes;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.abs.StatusEffect;

/**
 * 中毒效果
 * */
public class EffectPoison extends StatusEffect {
    public EffectPoison (String id) {
        super(id, Fight.UITexturePath("effect/poison.png"));
    }

    @Override
    public void applyEffectTick (LivingEntity<?> entity, int level) {
    }

    @Override
    public void applyEffectPreSecond (LivingEntity<?> entity, int level) {
        if (entity instanceof Player player) {
            player.applyDamage(DamageTypes.STATUS_EFFECT, new DamageSource(level) {
                @Override
                public float getDamage (LivingEntity<?> victim) {
                    return 2 * level;
                }
            });
        }else if (entity instanceof Enemy<?> enemy) {
            //对敌对实体来说，中毒就是治疗
            enemy.increaseHealth(level);
        }
    }
}

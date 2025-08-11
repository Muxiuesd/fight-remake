package ttk.muxiuesd.world.entity.effect;

import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.abs.StatusEffect;

/**
 * 治疗效果
 * */
public class EffectHealing extends StatusEffect {
    public EffectHealing (String id) {
        super(id);
    }

    @Override
    public void applyEffectTick (LivingEntity<?> entity, int level) {
        entity.increaseHealth(2 * level);
        System.out.println(entity + " 正在治愈");
    }
}

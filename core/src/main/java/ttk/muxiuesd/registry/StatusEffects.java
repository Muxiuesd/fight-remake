package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.entity.abs.StatusEffect;
import ttk.muxiuesd.world.entity.effect.EffectHealing;
import ttk.muxiuesd.world.entity.effect.EffectPoison;

/**
 * 所有状态效果的注册
 * */
public final class StatusEffects {
    public static void init() {}

    public static final StatusEffect HEALING = register(new EffectHealing(Identifier.of(Fight.ID("healing"))));
    public static final StatusEffect POISON = register(new EffectPoison(Identifier.of(Fight.ID("poison"))));



    public static StatusEffect register(StatusEffect effect) {
        return Registries.STATUS_EFFECT.register(effect.getIdentifier(), effect);
    }
}

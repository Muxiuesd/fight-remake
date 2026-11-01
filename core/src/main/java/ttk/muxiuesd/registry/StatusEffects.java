package ttk.muxiuesd.registry;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.entity.abs.StatusEffect;
import ttk.muxiuesd.world.entity.effect.EffectHealing;
import ttk.muxiuesd.world.entity.effect.EffectMoveSpeed;
import ttk.muxiuesd.world.entity.effect.EffectPoison;

import java.util.function.Function;

/**
 * 所有状态效果的注册
 * */
public final class StatusEffects {
    public static void init() {
        Log.print(StatusEffects.class.getName(), "所有状态效果已注册");
    }

    public static final StatusEffect HEALING    = register("healing", EffectHealing::new);
    public static final StatusEffect POISON     = register("poison", EffectPoison::new);
    public static final StatusEffect MOVE_SPEED = register("move_speed", EffectMoveSpeed::new);



    /**
     * 快捷注册
     * */
    public static StatusEffect register(String name, Function<Identifier, StatusEffect> function) {
        Identifier identifier = Identifier.of(Fight.ID(name));
        return register(function.apply(identifier));
    }

    /**
     * 最基础的状态效果注册
     * @param effect 已经指定了id标识符的状态效果实例
     * */
    public static StatusEffect register(StatusEffect effect) {
        return Registries.STATUS_EFFECT.register(effect.getIdentifier(), effect);
    }
}

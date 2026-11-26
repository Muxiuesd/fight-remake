package ttk.muxiuesd.registry;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.entity.abs.*;
import ttk.muxiuesd.world.entity.damage.DamageTypeBullet;
import ttk.muxiuesd.world.entity.damage.DamageTypeMelee;
import ttk.muxiuesd.world.entity.damage.DamageTypeStatusEffect;
import ttk.muxiuesd.world.entity.damage.DamageTypeSword;

import java.util.function.Function;

/**
 * 所有伤害类型
 * */
public final class DamageTypes {
    public static void init () {
        Log.print(DamageTypes.class.getName(), "游戏的伤害类型注册完毕");
    }

    public static final DamageType<Bullet, LivingEntity<?>> BULLET = register("bullet_damage", DamageTypeBullet::new);
    public static final DamageType<LivingEntity<?>, LivingEntity<?>> SWORD = register("sword_damage", DamageTypeSword::new);
    public static final DamageType<Enemy<?>, LivingEntity<?>> MELEE = register("melee_damage", DamageTypeMelee::new);
    public static final DamageType<StatusEffect.DamageSource, LivingEntity<?>> STATUS_EFFECT = register("status_effect", DamageTypeStatusEffect::new);

    public static <S, T extends LivingEntity<?>> DamageType<S, T> register (String name, Function<Identifier, DamageType<S, T>> function) {
        Identifier identifier = Identifier.of(Fight.NAMESPACE, name);
        DamageType<S, T> damageType = function.apply(identifier);
        Registries.DAMAGE_TYPE.register(identifier, damageType);
        return damageType;
    }
}

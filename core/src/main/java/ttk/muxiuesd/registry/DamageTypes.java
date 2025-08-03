package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.damage.DamageType;
import ttk.muxiuesd.world.entity.damage.DamageTypeBullet;
import ttk.muxiuesd.world.entity.damage.DamageTypeSword;

import java.util.function.Function;

/**
 * 所有伤害类型
 * */
public final class DamageTypes {
    public static void init () {
    }

    public static final DamageType<Bullet, LivingEntity<?>> BULLET = register("bullet_damage", DamageTypeBullet::new);
    public static final DamageType<LivingEntity<?>, LivingEntity<?>> SWORD = register("sword_damage", DamageTypeSword::new);


    public static <S, T> DamageType<S, T> register (String name, Function<Identifier, DamageType<S, T>> function) {
        Identifier identifier = new Identifier(Fight.NAMESPACE, name);
        DamageType<S, T> damageType = function.apply(identifier);
        Registries.DAMAGE_TYPE.register(identifier, damageType);
        return damageType;
    }
}

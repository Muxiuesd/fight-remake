package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.property.*;
import ttk.muxiuesd.registrant.Registries;

/**
 * 所有的属性
 * */
public final class PropertyTypes {

    public static final PropertyType<Integer> ITEM_MAX_COUNT = register("item_max_count", new PropertyItemMaxCount());
    public static final PropertyType<String> ITEM_USE_SOUND_ID = register("item_use_sound_id", new PropertyItemUseSoundID());
    public static final PropertyType<Float> WEAPON_DAMAGE = register("weapon_damage", new PropertyWeaponDamage());
    public static final PropertyType<Integer> WEAPON_DURATION = register("weapon_duration", new PropertyWeaponDuration());
    public static final PropertyType<Float> WEAPON_USE_SAPN = register("weapon_use_span", new PropertyWeaponUseSpan());


    public static <T> PropertyType<T> register (String name, PropertyType<T> type) {
        Identifier identifier = new Identifier(Fight.getId(name));
        return (PropertyType<T>) Registries.PROPERTY_TYPE.register(identifier, type).setName(name);
    }
}

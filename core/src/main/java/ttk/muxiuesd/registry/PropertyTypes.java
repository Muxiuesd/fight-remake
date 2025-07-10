package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.property.*;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.block.BlockSoundsID;
import ttk.muxiuesd.world.cat.CAT;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 所有的属性
 * <p>
 * 属性的使用方法：定义属性、注册属性、添加属性进物品或方块或实体
 * */
public final class PropertyTypes {
    public static void init () {
    }

    public static final PropertyType<CAT> CAT = register("cat", new PropertyCAT());
    public static final PropertyType<Integer> ITEM_MAX_COUNT = register("item_max_count", new DefaultIntPropertyType());
    public static final PropertyType<Boolean> ITEM_ON_USING = register("item_on_using", new DefaultBoolPropertyType());
    public static final PropertyType<String> ITEM_USE_SOUND_ID = register("item_use_sound_id", new DefaultStringPropertyType());
    public static final PropertyType<Entity> ITEM_WITH_ENTITY = register("item_with_entity", new PropertyItemWithEntity());

    public static final PropertyType<Float> WEAPON_USE_SAPN = register("weapon_use_span", new DefaultFloatPropertyType());
    public static final PropertyType<Float> WEAPON_DAMAGE = register("weapon_damage", new DefaultFloatPropertyType());
    public static final PropertyType<Float> WEAPON_ATTACK_RANGE = register("weapon_attack_range", new DefaultFloatPropertyType());
    public static final PropertyType<Integer> WEAPON_DURATION = register("weapon_duration", new DefaultIntPropertyType());

    public static final PropertyType<Float> BLOCK_FRICTON = register("block_friction", new DefaultFloatPropertyType());
    public static final PropertyType<BlockSoundsID> BLOCK_SOUNDS_ID = register("block_sounds", new PropertyBlockSoundsID());


    public static <T> PropertyType<T> register (String name, PropertyType<T> type) {
        Identifier identifier = new Identifier(Fight.getId(name));
        return (PropertyType<T>) Registries.PROPERTY_TYPE.register(identifier, type).setName(name);
    }
}

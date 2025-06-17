package ttk.muxiuesd.world.item.abs;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.JsonPropertiesMap;
import ttk.muxiuesd.data.abs.PropertiesDataMap;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

/**
 * 武器类
 * */
public abstract class Weapon extends Item{
    public static final PropertiesDataMap<?> DEFAULT_WEAPON_PROPERTIES_DATA_MAP = new JsonPropertiesMap()
        .add(PropertyTypes.ITEM_MAX_COUNT, 1)
        .add(PropertyTypes.ITEM_USE_SOUND_ID, Fight.getId("shoot"))
        .add(PropertyTypes.WEAPON_DAMAGE, 1f)
        .add(PropertyTypes.WEAPON_DURATION, 100)
        .add(PropertyTypes.WEAPON_USE_SAPN, 2f);
    //默认的武器物品的属性
    public static final Property DEFAULT_PROPERTY = new Property().setPropertiesMap(DEFAULT_WEAPON_PROPERTIES_DATA_MAP);

    public Weapon (Property property, String textureId, String texturePath) {
        super(Type.WEAPON, property, textureId, texturePath);
    }

    @Override
    public boolean use (World world, LivingEntity user) {
        return super.use(world, user);
    }

    /*public WeaponProperty getProperties () {
        return (WeaponProperty) property;
    }*/

    /**
     * 武器属性
     */
    /*public static class WeaponProperty extends Item.Property {
        public WeaponProperty () {
            //使用默认的数据映射
            setPropertiesMap(DEFAULT_WEAPON_PROPERTIES_DATA_MAP.copy());
        }

        public float getDamage () {
            return getPropertiesMap().get(PropertyTypes.WEAPON_DAMAGE);
        }

        public WeaponProperty setDamage (float damage) {
            getPropertiesMap().add(PropertyTypes.WEAPON_DAMAGE, damage);
            return this;
        }

        public int getDuration () {
            return getPropertiesMap().get(PropertyTypes.WEAPON_DURATION);
        }

        public WeaponProperty setDuration (int duration) {
            getPropertiesMap().add(PropertyTypes.WEAPON_DURATION, duration);
            return this;
        }

        public float getUseSpan () {
            return getPropertiesMap().get(PropertyTypes.WEAPON_USE_SAPN);
        }

        public WeaponProperty setUseSpan (float useSpan) {
            getPropertiesMap().add(PropertyTypes.WEAPON_USE_SAPN, useSpan);
            return this;
        }
    }*/
}

package ttk.muxiuesd.world.item.abs;

import ttk.muxiuesd.data.JsonPropertiesMap;
import ttk.muxiuesd.data.abs.PropertiesDataMap;
import ttk.muxiuesd.registry.PropertyTypes;

/**
 * 武器类
 * */
public abstract class Weapon extends Item {
    //这里面放一些不会被浅拷贝的值
    public static final PropertiesDataMap<?, ?, ?> DEFAULT_WEAPON_PROPERTIES_DATA_MAP = new JsonPropertiesMap()
        .add(PropertyTypes.ITEM_MAX_COUNT, 1)
        .add(PropertyTypes.WEAPON_DAMAGE, 1f)
        .add(PropertyTypes.WEAPON_DURATION, 100)
        .add(PropertyTypes.WEAPON_USE_SAPN, 1f);

    /**
     * 新建一个默认的武器物品的属性，里面放一些需要每一次都实例化一个对象的值，防止浅拷贝
     */
    public static Property createDefaultProperty() {
        return new Property().setPropertiesMap(DEFAULT_WEAPON_PROPERTIES_DATA_MAP.copy());
    }

    public Weapon (Property property, String textureId, String texturePath) {
        super(Type.WEAPON, property, textureId, texturePath);
    }
}

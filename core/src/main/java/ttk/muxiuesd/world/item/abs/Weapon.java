package ttk.muxiuesd.world.item.abs;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.JsonPropertiesMap;
import ttk.muxiuesd.data.abs.PropertiesDataMap;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 武器类
 * */
public abstract class Weapon extends Item {
    //这里面放一些不会被浅拷贝的值
    public static final PropertiesDataMap<?, ?, ?> DEFAULT_WEAPON_PROPERTIES_DATA_MAP = new JsonPropertiesMap()
        .add(PropertyTypes.ITEM_MAX_COUNT, 1)
        .add(PropertyTypes.WEAPON_DAMAGE, 1f)
        .add(PropertyTypes.ITEM_DURATION, 100)
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

    @Override
    public Array<Text> getTooltips (Array<Text> array, ItemStack itemStack) {
        array.add(Text.ofText(Fight.ID("weapon_use_span")).set(0, itemStack.getProperty().get(PropertyTypes.WEAPON_USE_SAPN)));
        array.add(Text.ofText(Fight.ID("weapon_damage")).set(0, itemStack.getProperty().get(PropertyTypes.WEAPON_DAMAGE)));
        return super.getTooltips(array, itemStack);
    }
}

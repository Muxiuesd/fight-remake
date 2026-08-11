package ttk.muxiuesd.world.item.abs;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 武器类
 * */
public abstract class Weapon extends Item {
    /**
     * 新建一个默认的武器物品的属性，里面放一些需要每一次都实例化一个对象的值，防止浅拷贝
     */
    public static Property createDefaultProperty() {
        return new Property()
            .add(PropertyTypes.ITEM_MAX_COUNT, 1)
            .add(PropertyTypes.WEAPON_DAMAGE, 1f)
            .add(PropertyTypes.ITEM_DURATION, 100)
            .add(PropertyTypes.WEAPON_USE_SAPN, 1f);
    }

    public Weapon (Property property) {
        super(Type.WEAPON, property);
    }

    @Override
    public Array<Text> getTooltips (Array<Text> array, ItemStack itemStack) {
        array.add(Text.ofText(Fight.ID("weapon_use_span")).set(0, itemStack.getProperty().get(PropertyTypes.WEAPON_USE_SAPN)));
        array.add(Text.ofText(Fight.ID("weapon_damage")).set(0, itemStack.getProperty().get(PropertyTypes.WEAPON_DAMAGE)));
        return super.getTooltips(array, itemStack);
    }
}

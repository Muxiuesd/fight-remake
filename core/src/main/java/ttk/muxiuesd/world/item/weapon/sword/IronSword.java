package ttk.muxiuesd.world.item.weapon.sword;

import ttk.muxiuesd.registry.PropertyTypes;

/**
 * 铁剑
 * */
@Deprecated
public class IronSword extends Sword {
    public IronSword () {
        super(Sword.createDefaultProperty()
                .add(PropertyTypes.WEAPON_DAMAGE, 3f)
                .add(PropertyTypes.WEAPON_USE_SAPN, 0.2f));
    }
}

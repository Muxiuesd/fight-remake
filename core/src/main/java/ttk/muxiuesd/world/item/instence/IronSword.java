package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.PropertyTypes;

/**
 * 铁剑
 * */
public class IronSword extends Sword {
    public IronSword () {
        super(Sword.createDefaultProperty()
                .add(PropertyTypes.WEAPON_DAMAGE, 3f)
                .add(PropertyTypes.WEAPON_USE_SAPN, 0.2f),
            Fight.ID("test_item"),
            Fight.ItemTexturePath("iron_sword.png"));
    }
}

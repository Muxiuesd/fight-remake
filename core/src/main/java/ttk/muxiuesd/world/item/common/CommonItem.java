package ttk.muxiuesd.world.item.common;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.registry.ItemStackBehaviours;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 非常普通的物品模板
 * */
public class CommonItem extends Item {
    public CommonItem (String name) {
        super(
            Type.COMMON, new Property(),
            Fight.ID(name), Fight.ItemTexturePath(name + ".png")
        );
    }

    @Override
    public IItemStackBehaviour getBehaviour () {
        return ItemStackBehaviours.COMMON;
    }
}

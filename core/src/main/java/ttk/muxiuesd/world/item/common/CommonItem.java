package ttk.muxiuesd.world.item.common;

import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.registry.ItemStackBehaviours;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 非常普通的物品模板
 * */
public class CommonItem extends Item {
    public CommonItem () {
        super(new Property());
    }

    @Override
    public IItemStackBehaviour getBehaviour () {
        return ItemStackBehaviours.COMMON;
    }
}

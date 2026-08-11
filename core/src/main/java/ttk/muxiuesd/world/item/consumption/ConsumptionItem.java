package ttk.muxiuesd.world.item.consumption;

import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.registry.ItemStackBehaviours;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 消费品的模板物品类（用一次数量消耗一个）
 * */
public class ConsumptionItem extends Item {

    public ConsumptionItem () {
        super(Type.CONSUMPTION, new Property());
    }
    public ConsumptionItem (Property property) {
        super(Type.CONSUMPTION, property);
    }

    @Override
    public IItemStackBehaviour getBehaviour () {
        return ItemStackBehaviours.CONSUMPTION;
    }
}

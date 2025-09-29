package ttk.muxiuesd.world.item.consumption;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.registry.ItemStackBehaviours;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 消费品模板（用一次数量消耗一个）
 * */
public class ConsumptionItem extends Item {
    public ConsumptionItem(String name) {
        super(Type.CONSUMPTION, new Property(), Fight.ID(name), Fight.ItemTexturePath(name + ".png"));
    }
    public ConsumptionItem (Property property, String textureId) {
        super(Type.CONSUMPTION, property, textureId);
    }
    public ConsumptionItem (Property property, String textureId, String texturePath) {
        super(Type.CONSUMPTION, property, textureId, texturePath);
    }

    @Override
    public IItemStackBehaviour getBehaviour () {
        return ItemStackBehaviours.CONSUMPTION;
    }
}

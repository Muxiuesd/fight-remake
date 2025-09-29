package ttk.muxiuesd.world.item.equipment;

import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.registry.ItemStackBehaviours;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 装备物品
 * TODO
 * */
public class EquipmentItem extends Item {
    public EquipmentItem (Type type, Property property, String textureId) {
        super(type, property, textureId);
    }

    public EquipmentItem (Type type, Property property, String textureId, String texturePath) {
        super(type, property, textureId, texturePath);
    }

    @Override
    public IItemStackBehaviour getBehaviour () {
        return ItemStackBehaviours.EQUIPMENT;
    }
}

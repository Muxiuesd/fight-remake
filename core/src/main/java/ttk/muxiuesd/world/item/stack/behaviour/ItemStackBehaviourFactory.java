package ttk.muxiuesd.world.item.stack.behaviour;

import ttk.muxiuesd.interfaces.IItemStackBehaviour;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 物品使用行为逻辑工厂类
 * */
public class ItemStackBehaviourFactory {
    public static IItemStackBehaviour create (Item.Type type) {
        //判断不同的物品类型来注入行为
        if (type == Item.Type.CONSUMPTION) return new ConsumptionItemStackBehaviour();
        else if (type == Item.Type.WEAPON) return new WeaponItemStackBehaviour();
        else if (type == Item.Type.EQUIPMENT) return new EquipmentItemStackBehaviour();

        //默认为普通物品
        return new CommonItemStackBehaviour();
    }
}

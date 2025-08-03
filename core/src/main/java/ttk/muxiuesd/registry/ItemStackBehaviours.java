package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.stack.behaviour.*;

/**
 * 物品使用行为逻辑(为了代码整洁才抽象出来的)
 * */
public final class ItemStackBehaviours {
    public static void init () {
    }

    public static final IItemStackBehaviour COMMON = register("common", new CommonItemStackBehaviour());
    public static final IItemStackBehaviour CONSUMPTION = register("consumption", new ConsumptionItemStackBehaviour());
    public static final IItemStackBehaviour SWORD = register("sword", new SwordItemStackBehaviour());
    public static final IItemStackBehaviour RANGED_WEAPON = register("ranged_weapon", new RangedWeaponItemStackBehaviour());
    public static final IItemStackBehaviour EQUIPMENT = register("equipment", new EquipmentItemStackBehaviour());


    public static IItemStackBehaviour create (Item.Type type) {
        //判断不同的物品类型来注入行为
        if (type == Item.Type.CONSUMPTION) return CONSUMPTION;
        else if (type == Item.Type.WEAPON) return SWORD;
        else if (type == Item.Type.EQUIPMENT) return EQUIPMENT;

        //默认为普通物品
        return COMMON;
    }

    public static IItemStackBehaviour register (String name, IItemStackBehaviour behaviour) {
        return Registries.ITEM_STACK_BEHAVIOUR.register(new Identifier(Fight.NAMESPACE, name), behaviour);
    }
}

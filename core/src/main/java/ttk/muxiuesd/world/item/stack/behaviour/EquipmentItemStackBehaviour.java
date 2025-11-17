package ttk.muxiuesd.world.item.stack.behaviour;

import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.equipment.EquipmentItem;

/**
 * 装备物品的使用行为
 * */
public class EquipmentItemStackBehaviour implements IItemStackBehaviour {
    /**
     * 手持装备物品使用时，自动装备上此装备，若对应槽位已经有装备则两者替换
     * */
    @Override
    public boolean use (World world, LivingEntity<?> user, ItemStack itemStack) {
        EquipmentItem equipment = (EquipmentItem) itemStack.getItem();
        int index = equipment.equipmentType.ordinal();
        Backpack equipmentBackpack = user.getEquipmentBackpack();
        ItemStack stack = equipmentBackpack.getItemStack(index);
        //根据装备类型的枚举来指定到装备的槽位
        equipmentBackpack.setItemStack(index, itemStack);

        int handIndex = user.getHandIndex();
        if (stack != null) {
            user.getBackpack().setItemStack(handIndex, stack);
        }else {
            user.getBackpack().clear(handIndex);
        }

        return itemStack.getItem().use(itemStack, world, user);
    }
}

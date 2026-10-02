package ttk.muxiuesd.world.item.stack.behaviour;

import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.equipment.EquipmentItem;
import ttk.muxiuesd.world.item.equipment.EquipmentType;

/**
 * 装备物品的使用行为
 * */
public class EquipmentItemStackBehaviour implements IItemStackBehaviour {
    /**
     * 手持装备物品使用时，自动装备上此装备，若对应槽位已经有装备则两者替换
     * */
    @Override
    public boolean use (World world, LivingEntity<?> user, ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (!(item instanceof EquipmentItem equipment)) {
            throw new IllegalStateException(
                "EQUIPMENT behaviour 绑定的物品不是 EquipmentItem，实际类型：" + item.getClass().getName());
        }

        //装备背包的格位由"槽位"决定：向玩家背包 UI 面板反查该装备类型对应的装备槽位下标
        int index = resolveEquipmentSlotIndex(equipment.getEquipmentType());
        if (index < 0) {
            //该装备类型没有配置装备槽位，没有可放置的位置
            return false;
        }

        Backpack equipmentBackpack = user.getEquipmentBackpack();
        ItemStack stack = equipmentBackpack.getItemStack(index);
        //放入对应的装备槽位
        equipmentBackpack.setItemStack(index, itemStack);

        int handIndex = user.getHandIndex();
        if (!stack.isVoid()) {
            user.getBackpack().setItemStack(handIndex, stack);
        }else {
            user.getBackpack().clear(handIndex);
        }

        return itemStack.getItem().use(itemStack, world, user);
    }

    /**
     * 反查该装备类型对应的装备背包下标
     * <p>
     * 装备槽位（位置与下标）由写 UI 的 {@code PlayerInventoryUIPanel} 决定，此处只做查询；
     * 未在 UI 上配置槽位的装备类型返回 -1。
     * */
    private int resolveEquipmentSlotIndex (EquipmentType type) {
        return PlayerSystem.PLAYER_INVENTORY_SCREEN.getInventoryUIPanel().getEquipmentSlotIndex(type);
    }
}

package ttk.muxiuesd.world.block.blockentity;

import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.InteractResult;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.interact.Slot;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 工作台方块实体
 * */
public class BlockEntityCraftingTable extends BlockEntity {
    public BlockEntityCraftingTable (World world, Block block, BlockPos blockPos) {
        super(world, block, blockPos, 9);
        setInteractGridSize(new GridPoint2(9, 9));
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(x + y * 3, x * 3, y * 3, 3, 3);
            }
        }
        System.out.println('a');
    }

    @Override
    public InteractResult interact (World world, LivingEntity<?> user, GridPoint2 interactGridPos) {
        //空手交互就是取出物品
        Inventory inventory = getInventory();
        if (inventory.isEmpty()) return InteractResult.FAILURE;

        Slot slot = this.getSlot(interactGridPos);
        //没有物品就跳过
        if (slot.getItemStack() == null) return InteractResult.FAILURE;
        ItemStack slotItemStack = slot.getItemStack();
        //按住左Shift就是把这个物品全数取出
        int outAmount = KeyBindings.PlayerShift.wasPressed() ? slotItemStack.getAmount() : 1;
        ItemStack outStack = slotItemStack.split(outAmount);
        user.setHandItemStack(outStack);
        inventory.clear();

        AudioPlayer.getInstance().playSound(Fight.getId("pop"));
        return InteractResult.SUCCESS;
    }

    @Override
    public InteractResult interactWithItem (World world, LivingEntity<?> user, ItemStack handItemStack, GridPoint2 interactGridPos) {
        //手持物品放入
        Slot slot = this.getSlot(interactGridPos);
        ItemStack slotItemStack = slot.getItemStack();
        if (slotItemStack == null) {
            //交互的槽位上本来没有物品
            int addAmount = KeyBindings.PlayerShift.wasPressed() ? handItemStack.getAmount() : 1;
            slot.setItemStack(handItemStack.split(addAmount));
        }else {
            //到这里就是槽位上本来有物品
            //检测交互槽位的物品是否与手持物品一致
            if (! handItemStack.equals(slotItemStack)) return InteractResult.FAILURE;
            //按住左Shift就是全部放进来
            int addAmount = KeyBindings.PlayerShift.wasPressed() ? handItemStack.getAmount() : 1;
            int afterAmount = addAmount + slotItemStack.getAmount();
            int maxCount = slotItemStack.getProperty().getMaxCount();
            //检查假如把手持的数量全部加进去是否超出限制，超过就重新设为上限值，没超过则还是原本的值
            if (afterAmount > maxCount) addAmount = maxCount;
            //按照指定数量增减
            handItemStack.decrease(addAmount);
            slotItemStack.increase(addAmount);
        }
        //记得清理
        user.backpack.clear();

        AudioPlayer.getInstance().playSound(Fight.getId("put"), 2.5f);
        return InteractResult.SUCCESS;
    }

    private void printInventory (Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItemStack(i);
            if (itemStack == null) continue;
            System.out.println(i + " : " + itemStack.getItem() + " : " + itemStack.getAmount());
        }
    }
}

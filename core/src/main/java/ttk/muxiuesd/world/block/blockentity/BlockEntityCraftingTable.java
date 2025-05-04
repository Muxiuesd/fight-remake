package ttk.muxiuesd.world.block.blockentity;

import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemClickBlockResult;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 工作台方块实体
 * */
public class BlockEntityCraftingTable extends BlockEntity {
    public BlockEntityCraftingTable (Block block, BlockPos blockPos) {
        super(block, blockPos);
    }

    @Override
    public void tick (float delta) {
        //System.out.println(10086);
    }

    @Override
    public void update (float delta) {
        super.update(delta);
    }

    @Override
    public void clickBlock (World world, LivingEntity user) {
        super.clickBlock(world, user);
    }

    @Override
    public ItemClickBlockResult clickBlockWithItem (World world, LivingEntity user, ItemStack handItemStack) {
        Inventory inventory = getInventory();
        //if (inventory.isFull()) return ItemClickBlockResult.FAILURE;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItemStack(i);
            if (itemStack == null) continue;
            System.out.println(i + " : " + itemStack.getItem());
        }
        //按住左Shift就是全部放进来
        int addAmount = KeyBindings.PlayerShift.wasJustPressed() ? 1 : handItemStack.getAmount();
        ItemStack bePutStack = new ItemStack(handItemStack.getItem(), addAmount);
        int oldAmount = bePutStack.getAmount();
        ItemStack stack = inventory.addItem(bePutStack);
        if (stack != null) {
            if (oldAmount == stack.getAmount()) {
                //数量没变就是满了装不下去
                System.out.println("放不进去哦");
                return ItemClickBlockResult.FAILURE;
            }
            handItemStack.setAmount(handItemStack.getAmount() - addAmount);
        }else {
            handItemStack.setAmount(handItemStack.getAmount() - addAmount);
        }

        for (int i = 0; i < inventory.getCapacity(); i++) {
            ItemStack itemStack = inventory.getItemStack(i);
            System.out.println("在：" + i + " 上有物品：" + itemStack.getItem() + " 数量：" + itemStack.getAmount());
        }
        System.out.println("玩家手持物品：" + handItemStack.getItem() + " 还剩：" + handItemStack.getAmount());
        return ItemClickBlockResult.SUCCESS;
    }
}

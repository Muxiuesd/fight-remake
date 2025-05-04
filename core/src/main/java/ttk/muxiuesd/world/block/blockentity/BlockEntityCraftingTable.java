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
        Inventory inventory = getInventory();
        if (inventory.isEmpty()) return;
        //空手交互就是取出物品，后进先出
        int index = inventory.getCapacity() - 1;
        ItemStack itemStack = inventory.getItemStack(index);
        if (itemStack == null) return;
        //按住左Shift就是把这个物品全数取出
        int outAmount = KeyBindings.PlayerShift.wasJustPressed() ? 1 : itemStack.getAmount();
        int oldAmount = itemStack.getAmount();
        ItemStack stack = user.backpack.addItem(new ItemStack(itemStack.getItem(), outAmount));

        itemStack.setAmount(oldAmount - outAmount);
        if (itemStack.getAmount() == 0) inventory.clear(index);

        printInventory(inventory);
        System.out.println("玩家：");
        printInventory(user.backpack);
    }

    @Override
    public ItemClickBlockResult clickBlockWithItem (World world, LivingEntity user, ItemStack handItemStack) {
        Inventory inventory = getInventory();
        printInventory(inventory);
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

        System.out.println("玩家：");
        printInventory(user.backpack);

        return ItemClickBlockResult.SUCCESS;
    }

    private void printInventory (Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItemStack(i);
            if (itemStack == null) continue;
            System.out.println(i + " : " + itemStack.getItem() + " : " + itemStack.getAmount());
        }
    }
}

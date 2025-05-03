package ttk.muxiuesd.world.block.blockentity;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
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
    public void clickBlockWithItem (World world, LivingEntity user, ItemStack handItemStack) {
        System.out.println(user + " clicked " + this + " by " + handItemStack.getItem());
    }
}

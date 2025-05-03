package ttk.muxiuesd.world.block.abs;

import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.interfaces.Tickable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 方块实体
 * */
public abstract class BlockEntity implements Updateable, Tickable {
    private Block block;    //方块
    private BlockPos blockPos;  //方块实体的位置
    private Inventory inventory;    //方块实体所拥有的容器


    public BlockEntity () {
    }

    /**
     * 点击一下方块（手上没东西）
     * */
    public void clickBlock (World world, LivingEntity user) {

    }

    /**
     * 手持物品点击方块
     * */
    public void clickBlockWithItem (World world, LivingEntity user, ItemStack handItemStack) {

    }

    /**
     * 方块实体每帧更新逻辑
     * */
    @Override
    public void update (float delta) {
    }

    /**
     * 方块实体每tick的更新逻辑
     * */
    @Override
    public void tick (float delta) {
    }

    public Block getBlock () {
        return block;
    }

    public BlockEntity setBlock (Block block) {
        this.block = block;
        return this;
    }

    public BlockPos getBlockPos () {
        return blockPos;
    }

    public BlockEntity setBlockPos (BlockPos blockPos) {
        this.blockPos = blockPos;
        return this;
    }

    public Inventory getInventory () {
        return inventory;
    }

    public BlockEntity setInventory (Inventory inventory) {
        this.inventory = inventory;
        return this;
    }
}

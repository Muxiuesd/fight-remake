package ttk.muxiuesd.world.block.blockentity;

import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.recipe.CraftingTableRecipe;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.BlockEntities;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.InteractResult;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.interact.InteractSlot;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 工作台方块实体
 * */
public class BlockEntityCraftingTable extends BlockEntity {
    public static final int OUTPUT_SLOT_INDEX = 9;

    public BlockEntityCraftingTable (BlockPos blockPos) {
        super(BlockEntities.CRAFTING_TABLE, blockPos);

        setInventory(new Backpack(10));
        setInteractGridSize(new GridPoint2(16, 16));
        int startX = 4;
        int startY = 6;
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(x + y * 3, startX + x * 3, startY + y * 3, 2, 2);
            }
        }

        addSlot(9, 7, 1, 2, 2);
    }

    @Override
    public InteractResult interact (World world, LivingEntity<?> user, GridPoint2 interactGridPos) {
        //空手交互就是取出物品
        Inventory inventory = getInventory();
        if (inventory.isEmpty()) return InteractResult.FAILURE;

        InteractSlot interactSlot = this.getSlot(interactGridPos);
        //没有物品就跳过
        if (interactSlot.getItemStack() == null) return InteractResult.FAILURE;

        //输入槽位的交互
        if (interactSlot.getIndex() != OUTPUT_SLOT_INDEX) {
            ItemStack slotItemStack = interactSlot.getItemStack();
            //按住左Shift就是把这个物品全数取出
            int outAmount = KeyBindings.PlayerShift.wasPressed() ? slotItemStack.getAmount() : 1;
            ItemStack outStack = slotItemStack.split(outAmount);
            user.setHandItemStack(outStack);
        } else {
            //取出输出槽位的东西
            //将输出槽位的物品复制一份到玩家手中
            user.setHandItemStack(interactSlot.getItemStack().copy(1));

            //就让输入槽位的东西都减一
            for (int i = 0; i < inventory.getSize(); i++) {
                ItemStack itemStack = inventory.getItemStack(i);
                if (itemStack == null) continue;
                itemStack.amountDecrease(1);
            }
        }

        //每次交互完之后更新一下输出槽位该有什么物品
        this.updateOutput();

        world.getSystem(SoundSystem.class).playSpatialSound(Sounds.ITEM_POP, getSounder());
        return InteractResult.SUCCESS;
    }

    @Override
    public InteractResult interactWithItem (World world, LivingEntity<?> user, ItemStack handItemStack, GridPoint2 interactGridPos) {
        //手持物品放入
        InteractSlot interactSlot = this.getSlot(interactGridPos);
        //输出槽位不放东西
        if (interactSlot == null || interactSlot.getIndex() == OUTPUT_SLOT_INDEX) return InteractResult.FAILURE;

        ItemStack slotItemStack = interactSlot.getItemStack();
        if (slotItemStack == null) {
            //交互的槽位上本来没有物品
            int addAmount = KeyBindings.PlayerShift.wasPressed() ? handItemStack.getAmount() : 1;
            interactSlot.setItemStack(handItemStack.split(addAmount));
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
            handItemStack.amountDecrease(addAmount);
            slotItemStack.amountIncrease(addAmount);
        }
        //记得清理
        user.getBackpack().clear();

        world.getSystem(SoundSystem.class).playSpatialSound(Sounds.ITEM_PUT, getSounder());

        this.updateOutput();
        return InteractResult.SUCCESS;
    }

    @Override
    public void tick (World world, float delta) {
        //后续不该这么写
        this.updateOutput();
        super.tick(world, delta);
    }

    /**
     * 更新输出槽位。根据输入槽位的物品来查找配方表
     * */
    public void updateOutput () {
        //暂时这么写，用于测试
        Inventory inventory = getInventory();
        //工作台的左下角槽位为0号
        ItemStack[] itemStacks = {
            inventory.getItemStack(6), inventory.getItemStack(7), inventory.getItemStack(8),
            inventory.getItemStack(3), inventory.getItemStack(4), inventory.getItemStack(5),
            inventory.getItemStack(0), inventory.getItemStack(1), inventory.getItemStack(2),
        };
        CraftingTableRecipe recipe = Registries.CRAFTING_RECIPE_REGISTRY.findRecipe(itemStacks);
        if (recipe != null) {
            ItemStack output = recipe.getOutput();
            //在输出槽位放对应的输出物品
            this.setOutputItemStack(output);
        }else {
            //没有对应的配方，就清空输出槽位
            this.setOutputItemStack(null);
        }
        //清理一下容器
        inventory.clear();
    }

    /**
     * 设置输出槽位的物品堆栈
     * */
    public void setOutputItemStack (ItemStack itemStack) {
        getSlots().get(OUTPUT_SLOT_INDEX).setItemStack(itemStack);
    }

    private void printInventory (Inventory inventory) {
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItemStack(i);
            if (itemStack == null) continue;
            System.out.println(i + " : " + itemStack.getItem() + " : " + itemStack.getAmount());
        }
    }
}

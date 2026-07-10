package ttk.muxiuesd.world.block.blockentity;

import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.recipe.CraftingTableRecipe;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.BlockEntities;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.InteractResult;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.genfactory.ItemEntityGetter;
import ttk.muxiuesd.world.interact.InteractSlot;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 工作台方块实体
 * */
public class BlockEntityCraftingTable extends BlockEntity {
    public BlockEntityCraftingTable (BlockPos blockPos) {
        super(BlockEntities.CRAFTING_TABLE, blockPos);

        setInventory(new Backpack(9));
        setInteractGridSize(new GridPoint2(9, 9));
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                addSlot(x + y * 3, x * 3, y * 3, 3, 3);
            }
        }
    }

    @Override
    public InteractResult interact (World world, LivingEntity<?> user, GridPoint2 interactGridPos) {
        //空手交互就是取出物品
        Inventory inventory = getInventory();
        if (inventory.isEmpty()) return InteractResult.FAILURE;

        InteractSlot interactSlot = this.getSlot(interactGridPos);
        //没有物品就跳过
        if (interactSlot.getItemStack() == null) return InteractResult.FAILURE;

        ItemStack slotItemStack = interactSlot.getItemStack();
        //按住左Shift就是把这个物品全数取出
        int outAmount = KeyBindings.PlayerShift.wasPressed() ? slotItemStack.getAmount() : 1;
        ItemStack outStack = slotItemStack.split(outAmount);
        user.setHandItemStack(outStack);
        inventory.clear();

        world.getSystem(SoundSystem.class).playSpatialSound(Sounds.ITEM_POP, getSounder());
        return InteractResult.SUCCESS;
    }

    @Override
    public InteractResult interactWithItem (World world, LivingEntity<?> user, ItemStack handItemStack, GridPoint2 interactGridPos) {
        //手持物品放入
        InteractSlot interactSlot = this.getSlot(interactGridPos);
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

        //暂时这么写测试一下
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
            ItemEntity itemEntity = ItemEntityGetter.get(world.getSystem(EntitySystem.class), getBlockPos(), output);

            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.dropItem(i, 1);
            }
            inventory.clear();
        }


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

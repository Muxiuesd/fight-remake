package ttk.muxiuesd.world.block.blockentity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.system.LightSystem;
import ttk.muxiuesd.system.ParticleSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.InteractResult;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.instance.BlockFurnace;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.interact.Slot;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.ItemsReg;
import ttk.muxiuesd.world.light.PointLight;
import ttk.muxiuesd.world.particle.ParticleEmittersReg;

/**
 * 熔炉
 * */
public class BlockEntityFurnace extends BlockEntity {
    private int curTick = 0;
    private Slot inputSlot;
    private Slot outputSlot;
    private Slot fuelSlot;
    private PointLight light;

    public BlockEntityFurnace (World world, Block block, BlockPos blockPos) {
        super(world, block, blockPos, 3);
        this.inputSlot = new Slot(new GridPoint2(1, 8), new GridPoint2(6,6), this.getInputSlotIndex());
        this.outputSlot = new Slot(new GridPoint2(9, 8), new GridPoint2(6,6), this.getOutputSlotIndex());
        this.fuelSlot = new Slot(new GridPoint2(5, 0), new GridPoint2(6,6), this.getFuelSlotIndex());
        this.light = new PointLight(new Color(0.8f, 0.1f, 0.1f, 0.1f), 2.5f);
        this.light.setPosition(new Vector2(blockPos).add(0.5f, 0.2f));
    }

    @Override
    public InteractResult interactWithItem (World world, LivingEntity user, ItemStack handItemStack, GridPoint2 interactGridPos) {
        //TODO 根据物品类型或者配方来判断是否可以把东西放进来当原料或者燃料

        Slot interactSlot = this.getSlot(interactGridPos);
        //没碰到任何槽位
        if (interactSlot == null) return InteractResult.FAILURE;
        System.out.println("交互槽位：" + interactSlot.getIndex());
        //输出槽位不能放物品进来
        if (interactSlot == this.outputSlot) return InteractResult.FAILURE;

        Inventory inventory = getInventory();
        ItemStack interactStack = inventory.getItemStack(interactSlot.getIndex());
        int addAmount = KeyBindings.PlayerShift.wasPressed() ? handItemStack.getAmount() : 1;
        ItemStack bePutStack = new ItemStack(handItemStack.getItem(), addAmount);

        if (interactStack == null) {
            //如果槽位上没东西，就直接加入
            inventory.setItemStack(interactSlot.getIndex(), bePutStack);
            handItemStack.setAmount(handItemStack.getAmount() - addAmount);
            //handItemStack.setAmount(handItemStack.getAmount() - addAmount);
        }else if (interactStack.getAmount() == interactStack.getProperty().getMaxCount()) {
            //槽位上的物品数量满了
            return InteractResult.FAILURE;
        } else {
            //如果槽位上有东西且数量没满
            //手持物品与槽位上的物品不同也是交互失败
            if (interactStack.getItem() != handItemStack.getItem()) return InteractResult.FAILURE;
            //到这里就是相同物品
            int bePutStackAmount = bePutStack.getAmount();
            int interactStackAmount = interactStack.getAmount();
            int newAmount = bePutStackAmount + interactStackAmount;
            int maxCount = interactStack.getProperty().getMaxCount();
            if (newAmount > maxCount) {
                //如果超出数量，就计算需要的数量
                int needAmount = maxCount - interactStackAmount;
                interactStack.setAmount(interactStackAmount + needAmount);
                bePutStack.setAmount(bePutStack.getAmount() - needAmount);
            }else {
                //如果数量没超出
                interactStack.setAmount(newAmount);
                bePutStack.setAmount(0);
            }
            handItemStack.setAmount(handItemStack.getAmount() - addAmount - bePutStack.getAmount());
        }

        AudioPlayer.getInstance().playSound(Fight.getId("put"), 2.5f);
        return InteractResult.SUCCESS;
    }

    @Override
    public InteractResult interact (World world, LivingEntity user, GridPoint2 interactGridPos) {
        Inventory inventory = getInventory();
        if (inventory.isEmpty()) return InteractResult.FAILURE;
        //取出物品

        return InteractResult.SUCCESS;
    }

    @Override
    public void tick (World world, float delta) {
        //TODO 熔炉的熔炼配方

        if (this.isWorking() && MathUtils.random() < 0.05f) {
            ParticleSystem ps = (ParticleSystem) world.getSystemManager().getSystem("ParticleSystem");
            ps.emitParticle(ParticleEmittersReg.FURNACE_FIRE, MathUtils.random(1, 3),
                new Vector2(getBlockPos()).add(0.5f, 0), new Vector2(0, 1.7f), new Vector2(),
                new Vector2(0.5f, 0.5f), new Vector2(0.05f, 0.05f), new Vector2(1f ,1f),
                0f, 2.2f);
        }

        Inventory inventory = getInventory();
        ItemStack inputStack = inventory.getItemStack(this.getInputSlotIndex());
        ItemStack fuelStack = inventory.getItemStack(this.getFuelSlotIndex());
        if (inputStack != null && fuelStack != null) {
            if (this.curTick < 100) {
                this.curTick++;
                this.setWorking(true);
                return;
            }
            //产出
            this.curTick = 0;
            inputStack.setAmount(inputStack.getAmount() - 1);
            fuelStack.setAmount(fuelStack.getAmount() - 1);
            ItemStack outputStack = inventory.getItemStack(this.getOutputSlotIndex());
            if (outputStack == null) {
                inventory.setItemStack(this.getOutputSlotIndex(), new ItemStack(ItemsReg.RUBBISH, 1));
            }else {
                outputStack.setAmount(outputStack.getAmount() + 1);
            }
            if (inputStack.getAmount() == 0) inventory.clear(this.getInputSlotIndex());
            if (fuelStack.getAmount() == 0) inventory.clear(this.getFuelSlotIndex());
            return;
        }
        this.setWorking(false);
    }

    @Override
    public void update (float delta) {
        //应用光源
        if (this.isWorking()) {
            LightSystem lightSystem = (LightSystem) getWorld().getSystemManager().getSystem("LightSystem");
            lightSystem.useLight(this.light);
        }
    }

    @Override
    public void draw (Batch batch, float x, float y) {
        Inventory inventory = getInventory();
        if (inventory.isEmpty()) return;
        GridPoint2 interactGridSize = getInteractGridSize();
        ItemStack inputStack = inventory.getItemStack(getInputSlotIndex());
        if (inputStack != null) {
            GridPoint2 startPos = this.inputSlot.getStartPos();
            GridPoint2 size = this.inputSlot.getSize();

            float slotX = x + (float) startPos.x / interactGridSize.x;
            float slotY = y + (float) startPos.y / interactGridSize.y;
            float slotWidth  = (float) size.x / interactGridSize.x;
            float slotHeight = (float) size.y / interactGridSize.y;
            batch.draw(inputStack.getItem().texture, slotX, slotY, slotWidth, slotHeight);
        }
        ItemStack outputStack = inventory.getItemStack(getOutputSlotIndex());
        if (outputStack != null) {
            GridPoint2 startPos = this.outputSlot.getStartPos();
            GridPoint2 size = this.outputSlot.getSize();
            float slotX = x + (float) startPos.x / interactGridSize.x;
            float slotY = y + (float) startPos.y / interactGridSize.y;
            float slotWidth  = (float) size.x / interactGridSize.x;
            float slotHeight = (float) size.y / interactGridSize.y;
            batch.draw(outputStack.getItem().texture, slotX, slotY, slotWidth, slotHeight);
        }
        ItemStack fuelStack = inventory.getItemStack(getFuelSlotIndex());
        if (fuelStack != null) {
            GridPoint2 startPos = this.fuelSlot.getStartPos();
            GridPoint2 size = this.fuelSlot.getSize();
            float slotX = x + (float) startPos.x / interactGridSize.x;
            float slotY = y + (float) startPos.y / interactGridSize.y;
            float slotWidth  = (float) size.x / interactGridSize.x;
            float slotHeight = (float) size.y / interactGridSize.y;
            batch.draw(fuelStack.getItem().texture, slotX, slotY, slotWidth, slotHeight);
        }
    }

    public int getCurTick () {
        return this.curTick;
    }

    public void setCurTick (int curTick) {
        this.curTick = curTick;
    }

    public int getInputSlotIndex () {
        return 0;
    }

    public int getOutputSlotIndex () {
        return 1;
    }

    public int getFuelSlotIndex () {
        return 2;
    }

    public Slot getSlot (GridPoint2 interactGridPos) {
        if (interactGridPos == null) return null;
        if (this.inputSlot.touch(interactGridPos)) return this.inputSlot;
        if (this.outputSlot.touch(interactGridPos)) return this.outputSlot;
        if (this.fuelSlot.touch(interactGridPos)) return this.fuelSlot;
        return null;
    }

    public boolean isWorking () {
        BlockFurnace furnace = (BlockFurnace) getBlock();
        return furnace.isWorking();
    }

    public void setWorking (boolean working) {
        BlockFurnace furnace = (BlockFurnace) getBlock();
        furnace.setWorking(working);
    }
}

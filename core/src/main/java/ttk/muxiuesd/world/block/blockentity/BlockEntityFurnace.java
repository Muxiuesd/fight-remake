package ttk.muxiuesd.world.block.blockentity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.registry.Fuels;
import ttk.muxiuesd.registry.FurnaceRecipes;
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
import ttk.muxiuesd.world.light.PointLight;
import ttk.muxiuesd.world.particle.ParticleEmittersReg;

/**
 * 熔炉
 * */
public class BlockEntityFurnace extends BlockEntity {
    private int curEnergy = 0;  //能量，每tick减1
    private int curTick = 0;
    private final Slot inputSlot;
    private final Slot outputSlot;
    private final Slot fuelSlot;
    private PointLight light;

    public BlockEntityFurnace (World world, Block block, BlockPos blockPos) {
        super(world, block, blockPos, 3);

        this.inputSlot = addSlot(this.getInputSlotIndex(), 1, 8, 6, 6);
        this.outputSlot = addSlot(this.getOutputSlotIndex(), 9, 8, 6, 6);
        this.fuelSlot = addSlot(this.getFuelSlotIndex(), 5, 0, 6, 6);

        this.light = new PointLight(new Color(0.8f, 0.1f, 0.1f, 0.1f), 2.5f);
        this.light.setPosition(new Vector2(blockPos).add(0.5f, 0.2f));
    }

    @Override
    public InteractResult interactWithItem (World world, LivingEntity<?> user, ItemStack handItemStack, GridPoint2 interactGridPos) {
        //TODO 根据物品类型或者配方来判断是否可以把东西放进来当原料或者燃料

        Slot interactSlot = getSlot(interactGridPos);
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
    public InteractResult interact (World world, LivingEntity<?> user, GridPoint2 interactGridPos) {
        Inventory inventory = getInventory();
        if (inventory.isEmpty()) return InteractResult.FAILURE;
        //获取交互槽位
        Slot interactSlot = getSlot(interactGridPos);
        //没有交互到槽位
        if (interactSlot == null) return InteractResult.FAILURE;
        //到这里说明交互到了槽位
        ItemStack interactStack = inventory.getItemStack(interactSlot.getIndex());
        int outAmount = KeyBindings.PlayerShift.wasPressed() ? interactStack.getAmount() : 1;
        //丢出物品
        ItemStack outStack = inventory.dropItem(interactSlot.getIndex(), outAmount);

        user.setHandItemStack(outStack);
        inventory.clear();

        return InteractResult.SUCCESS;
    }

    @Override
    public void tick (World world, float delta) {
        this.workingParticle(world);

        Inventory inventory = getInventory();
        ItemStack inputStack = inventory.getItemStack(this.getInputSlotIndex());
        ItemStack fuelStack = inventory.getItemStack(this.getFuelSlotIndex());
        //输入槽位有物品
        if (inputStack != null) {
            //先检查配方
            if (!FurnaceRecipes.has(inputStack)) {
                //没有配方就直接跳过
                this.setWorking(false);
                return;
            }

            //熔炉的输出
            ItemStack outputStack = inventory.getItemStack(this.getOutputSlotIndex());
            //从配方表中获取输出结果
            ItemStack resultStack = FurnaceRecipes.getOutput(inputStack);
            //输出的位置不空
            if (outputStack != null) {
                //满了就直接跳过
                if (outputStack.isFull()) return;
                //输出和结果不一样也跳过
                if (resultStack.getItem() != outputStack.getItem()) return;
            }
            //到这里就是输出槽位没东西或者与配方结果相同且数量没有达到上限
            //检查能量值
            if (this.curEnergy == 0) {
                //没有燃料则跳过
                if (fuelStack == null) return;
                int energy = Fuels.get(fuelStack.getItem());
                if (energy == 0) {
                    //能量没有增加成功，也直接跳过
                    return;
                }
                //消耗物品增加能量值
                fuelStack.fastDecrease();
                this.curEnergy += energy;
            }
            //到这里就是可以开始工作了
            if (this.curTick < 60) {
                //烧炼进行时
                this.curTick++;
                this.curEnergy--;
                this.setWorking(true);

            }else {
                //到这里就是一次烧炼完成
                //产出
                this.curTick = 0;
                inputStack.fastDecrease();

                if (outputStack == null) {
                    //输出槽位位空时
                    inventory.setItemStack(this.getOutputSlotIndex(), resultStack.copy(1));
                } else {
                    //输出槽位不为空但物品相同
                    outputStack.setAmount(outputStack.getAmount() + 1);
                }
            }
            inventory.clear();
        }else {
            //输入槽位没物品时
            if (this.curEnergy > 0) {
                this.setWorking(true);
                this.curEnergy--;
            } else {
                this.setWorking(false);
            }
        }
    }

    /**
     * 工作时的粒子效果
     * */
    public void workingParticle (World world) {
        if (this.isWorking() && MathUtils.random() < 0.07f) {
            ParticleSystem ps = (ParticleSystem) world.getSystemManager().getSystem("ParticleSystem");
            ps.emitParticle(ParticleEmittersReg.FURNACE_FIRE, MathUtils.random(1, 3),
                new Vector2(getBlockPos()).add(0.45f, 0), new Vector2(0, 1.7f), new Vector2(),
                new Vector2(0.5f, 0.5f), new Vector2(0.05f, 0.05f), new Vector2(1f ,1f),
                0f, 2.2f);
        }
    }


    @Override
    public void update (float delta) {
        //应用光源
        if (this.isWorking()) {
            LightSystem lightSystem = (LightSystem) getWorld().getSystemManager().getSystem("LightSystem");
            lightSystem.useLight(this.light);
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

    /*public Slot getSlot (GridPoint2 interactGridPos) {
        if (interactGridPos == null) return null;
        if (this.inputSlot.touch(interactGridPos)) return this.inputSlot;
        if (this.outputSlot.touch(interactGridPos)) return this.outputSlot;
        if (this.fuelSlot.touch(interactGridPos)) return this.fuelSlot;
        return null;
    }*/

    public boolean isWorking () {
        BlockFurnace furnace = (BlockFurnace) getBlock();
        return furnace.isWorking();
    }

    public void setWorking (boolean working) {
        BlockFurnace furnace = (BlockFurnace) getBlock();
        furnace.setWorking(working);
    }
}

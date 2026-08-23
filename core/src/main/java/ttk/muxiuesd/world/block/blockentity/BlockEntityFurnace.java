package ttk.muxiuesd.world.block.blockentity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.Codecable;
import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.registry.BlockEntities;
import ttk.muxiuesd.registry.Fuels;
import ttk.muxiuesd.registry.FurnaceRecipes;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.serialization.codecs.builders.BlockEntityCodecBuilder;
import ttk.muxiuesd.system.LightSystem;
import ttk.muxiuesd.system.ParticleSystem;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.InteractResult;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.instance.BlockFurnace;
import ttk.muxiuesd.world.cat.CatInt;
import ttk.muxiuesd.world.cat.CatsHolder;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.interact.InteractSlot;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.light.PointLight;
import ttk.muxiuesd.world.particle.ParticleEmittersReg;

/**
 * 熔炉
 * */
public class BlockEntityFurnace extends BlockEntity implements Codecable<BlockEntityFurnace> {
    /*public static final Codec<BlockEntityFurnace> CODEC1 = CodecBuilder.<BlockEntityFurnace>create()
        .paramField("block_pos", BlockEntity::getBlockPos, BlockPos.CODEC)
        .paramField("id", (entity -> entity.getProvider().getID()), Codec.STRING)
        .field("inventory", BlockEntity::getInventory, BlockEntity::setInventory, Backpack.CODEC)
        .factory((blockPos, id) -> {
            //从注册表拿到方块实体的提供者
            BlockEntityProvider<?> provider = Registries.BLOCK_ENTITY.get(id);
            BlockEntity blockEntity = provider.create(blockPos);
            blockEntity.setProvider(provider);
            return (BlockEntityFurnace) blockEntity;
        });*/

    public static final Codec<BlockEntityFurnace> CODEC = BlockEntityCodecBuilder
        .create(builder ->
            builder
                .field("curEnergy", BlockEntityFurnace::getCurEnergy, BlockEntityFurnace::setCurEnergy, Codec.INT)
                .field("curTick", BlockEntityFurnace::getCurTick, BlockEntityFurnace::setCurTick, Codec.INT)
    );


    private int curEnergy = 0;  //能量，每tick减1
    private int curTick = 0;
    private InteractSlot inputInteractSlot;
    private InteractSlot outputInteractSlot;
    private InteractSlot fuelInteractSlot;
    private PointLight light;

    public BlockEntityFurnace (BlockPos blockPos) {
        super(BlockEntities.FURNACE, blockPos);
        setInventory(new Backpack(3));

        this.inputInteractSlot = addSlot(this.getInputSlotIndex(), 1, 8, 6, 6);
        this.outputInteractSlot = addSlot(this.getOutputSlotIndex(), 9, 8, 6, 6);
        this.fuelInteractSlot = addSlot(this.getFuelSlotIndex(), 5, 0, 6, 6);

        this.light = new PointLight(new Color(0.8f, 0.1f, 0.1f, 0.1f), 2.5f);
        this.light.setPosition(new Vector2(blockPos).add(0.5f, 0.2f));
    }

    @Override
    public void writeCatData (CatsHolder holder) {
        super.writeCatData(holder);
        holder.put("curEnergy", new CatInt(this.curEnergy));
        holder.put("curTick", new CatInt(this.curTick));
    }

    @Override
    public void readCatData (JsonValue values) {
        super.readCatData(values);
        this.curEnergy = values.getInt("curEnergy", 0);
        this.curTick = values.getInt("curTick", 0);
    }


    @Override
    public InteractResult interactWithItem (World world, LivingEntity<?> user, ItemStack handItemStack, GridPoint2 interactGridPos) {
        //TODO 根据物品类型或者配方来判断是否可以把东西放进来当原料或者燃料

        InteractSlot interactSlot = getSlot(interactGridPos);
        //没碰到任何槽位
        if (interactSlot == null) return InteractResult.FAILURE;
        System.out.println("交互槽位：" + interactSlot.getIndex());
        //输出槽位不能放物品进来
        if (interactSlot == this.outputInteractSlot) return InteractResult.FAILURE;

        Inventory inventory = getInventory();
        ItemStack interactStack = inventory.getItemStack(interactSlot.getIndex());
        int addAmount = KeyBindings.PlayerShift.wasPressed() ? handItemStack.getAmount() : 1;
        ItemStack bePutStack = new ItemStack(handItemStack.getItem(), addAmount);

        if (interactStack.isVoid()) {
            //如果槽位上没东西，就直接加入
            inventory.setItemStack(interactSlot.getIndex(), bePutStack);
            handItemStack.setAmount(handItemStack.getAmount() - addAmount);
        }else if (interactStack.equals(handItemStack)) {
            //同物品：合并（槽满则无法放入）
            if (interactStack.getAmount() == interactStack.getProperty().getMaxCount()) {
                return InteractResult.FAILURE;
            }
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
            //手部扣减 = 尝试放入量 - 放不下的剩余部分 = 实际放入量（保证数量守恒）
            handItemStack.setAmount(handItemStack.getAmount() - addAmount + bePutStack.getAmount());
        } else {
            //不同物品：交换（手部物品放入槽位，槽位物品拿回手上）
            inventory.setItemStack(interactSlot.getIndex(), handItemStack);
            user.setHandItemStack(interactStack);
        }

        world.getSystem(SoundSystem.class).playSpatialSound(Sounds.ITEM_PUT, getSounder());
        return InteractResult.SUCCESS;
    }

    @Override
    public InteractResult interact (World world, LivingEntity<?> user, GridPoint2 interactGridPos) {
        Inventory inventory = getInventory();
        if (inventory.isEmpty()) return InteractResult.FAILURE;
        //获取交互槽位
        InteractSlot interactSlot = getSlot(interactGridPos);
        //没有交互到槽位
        if (interactSlot == null) return InteractResult.FAILURE;
        //到这里说明交互到了槽位
        ItemStack interactStack = inventory.getItemStack(interactSlot.getIndex());
        //点击空槽不处理（否则 dropItem 返回空堆叠会把玩家手持物品清空）
        if (interactStack.isVoid()) return InteractResult.FAILURE;
        int outAmount = KeyBindings.PlayerShift.wasPressed() ? interactStack.getAmount() : 1;
        //丢出物品
        ItemStack outStack = inventory.dropItem(interactSlot.getIndex(), outAmount);

        user.setHandItemStack(outStack);
        inventory.clear();

        world.getSystem(SoundSystem.class).playSpatialSound(Sounds.ITEM_POP, getSounder());

        return InteractResult.SUCCESS;
    }

    @Override
    public void tick (World world, float delta) {
        this.workingParticle(world);

        Inventory inventory = getInventory();
        ItemStack inputStack = inventory.getItemStack(this.getInputSlotIndex());
        ItemStack fuelStack = inventory.getItemStack(this.getFuelSlotIndex());
        //输入槽位有物品
        if (!inputStack.isVoid()) {
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
            if (!outputStack.isVoid()) {
                //满了就直接跳过
                if (outputStack.isFull()) return;
                //输出和结果不一样也跳过
                if (resultStack.getItem() != outputStack.getItem()) return;
            }
            //到这里就是输出槽位没东西或者与配方结果相同且数量没有达到上限
            //检查能量值
            if (this.curEnergy == 0) {
                //没有燃料则跳过
                if (fuelStack.isVoid()) return;
                int energy = Fuels.get(fuelStack.getItem());
                if (energy == 0) {
                    //能量没有增加成功，也直接跳过
                    return;
                }
                //消耗物品增加能量值
                fuelStack.amountFastDecrease();
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
                inputStack.amountFastDecrease();

                if (outputStack.isVoid()) {
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
            ParticleSystem ps = world.getSystemManager().getSystem(ParticleSystem.class);
            //TODO 有些常量可以提出，减少new的次数
            ps.emitParticle(ParticleEmittersReg.FURNACE_FIRE, MathUtils.random(1, 3),
                new Vector2(getBlockPos()).add(0, -0.42f), new Vector2(0, 0.6f), new Vector2(),
                new Vector2(0.2f, 0.2f), new Vector2(0.05f, 0.05f), new Vector2(1f ,1f),
                0f, 2.2f);
        }
    }


    @Override
    public void update (float delta) {
        //应用光源
        if (this.isWorking()) {
            LightSystem lightSystem = getWorld().getSystem(LightSystem.class);
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

    public boolean isWorking () {
        BlockFurnace furnace = (BlockFurnace) getBlock();
        return furnace.isWorking();
    }

    public void setWorking (boolean working) {
        BlockFurnace furnace = (BlockFurnace) getBlock();
        furnace.setWorking(working);
    }

    public int getCurEnergy () {
        return curEnergy;
    }

    public BlockEntityFurnace setCurEnergy (int curEnergy) {
        this.curEnergy = curEnergy;
        return this;
    }

    @Override
    public Codec<BlockEntityFurnace> getCodec () {
        return CODEC;
    }
}

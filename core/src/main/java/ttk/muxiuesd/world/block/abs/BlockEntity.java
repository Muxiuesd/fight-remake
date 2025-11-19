package ttk.muxiuesd.world.block.abs;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.ICAT;
import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.interfaces.Tickable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.InteractResult;
import ttk.muxiuesd.world.block.blockentity.BlockEntityProvider;
import ttk.muxiuesd.world.cat.CAT;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.interact.Slot;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 方块实体
 * */
public abstract class BlockEntity implements Updateable, Tickable, ICAT {
    private BlockEntityProvider<? extends BlockEntity> provider;
    private World world;                    //方块实体所属的世界
    private BlockWithEntity block;          //方块
    private BlockPos blockPos;              //方块实体的位置
    private GridPoint2 interactGridSize;    //方块实体的交互网格大小
    private Inventory inventory;            //方块实体所拥有的容器
    private List<Slot> slots;               //交互槽位


    public BlockEntity (BlockEntityProvider<?> blockEntityProvider, BlockPos blockPos) {
        this(blockPos);
        this.provider = blockEntityProvider;
    }
    private BlockEntity (BlockPos blockPos) {
        this.blockPos = blockPos;

        this.setInteractGridSize(new GridPoint2(16, 16));
        this.slots = new ArrayList<>();
    }

    /**
     * 由所属方块调用的写入
     * */
    @Override
    public void writeCAT (CAT cat) {
    }

    /**
     * 由所属方块调用的读取
     * */
    @Override
    public void readCAT (JsonValue values) {
    }

    /**
     * 空手与方块互动
     * */
    public InteractResult interact (World world, LivingEntity<?> user, GridPoint2 interactGridPos) {
        return InteractResult.FAILURE;
    }

    /**
     * 手持物品与方块互动
     * */
    public InteractResult interactWithItem (World world, LivingEntity<?> user, ItemStack handItemStack, GridPoint2 interactGridPos) {
        return InteractResult.FAILURE;
    }

    /**
     * 方块实体被放置
     * */
    public void bePlaced (World world, Player player) {

    }

    /**
     * 方块实体被破坏
     * */
    public void beDestroyed (World world, Player destroyer) {
        EntitySystem es = world.getSystemManager().getSystem(EntitySystem.class);
        //掉落物品
        for (int i = 0; i < this.getInventory().getSize(); i++) {
            ItemStack itemStack = this.getInventory().getItemStack(i);
            if (itemStack == null) continue;

            itemStack.getItem().beDropped(itemStack, world, destroyer);
            //ItemEntity itemEntity = (ItemEntity) Gets.ENTITY(Fight.getId("item_entity"), es);
            ItemEntity itemEntity = Pools.ITEM_ENTITY.obtain();
            itemEntity.setEntitySystem(es);
            itemEntity.setItemStack(itemStack);
            itemEntity.setPosition(getBlockPos());
            itemEntity.setSize(ItemEntity.DEFAULT_SIZE);
            itemEntity.setOnGround(false);
            itemEntity.setOnAirTimer(new TaskTimer(0.2f, 0, () -> itemEntity.setOnAirTimer(null)));
            float speed = MathUtils.random(2f, 3.5f);
            double radian = Util.randomRadian();
            itemEntity.setSpeed(speed);
            itemEntity.setVelocity(new Vector2((float) Math.cos(radian), (float) Math.sin(radian)));
            itemEntity.setLivingTime(Fight.ITEM_ENTITY_PICKUP_SPAN.getValue());

            this.getInventory().clear(i);
        }
    }

    /**
     * 添加slot
     * */
    public Slot addSlot (int index, int x, int y, int width, int height) {
        return this.addSlot(new Slot(new GridPoint2(x, y), new GridPoint2(width, height), index));
    }

    /**
     * 添加一个slot
     * */
    public Slot addSlot (Slot slot) {
        if (!this.slots.contains(slot)) {
            this.slots.add(slot);
            slot.setInventory(this.inventory);
        }
        return slot;
    }

    /**
     * 移除slot
     * */
    public BlockEntity removeSlot (Slot slot) {
        this.slots.remove(slot);
        return this;
    }

    /**
     * 获取slot
     * */
    public Slot getSlot (GridPoint2 interactGridPos) {
        for (Slot slot : this.slots) {
            if (slot.touch(interactGridPos)) return slot;
        }
        return null;
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
    public void tick (World world, float delta) {
    }

    public BlockEntityProvider<? extends BlockEntity> getProvider () {
        return provider;
    }

    public BlockEntity setProvider (BlockEntityProvider<? extends BlockEntity> provider) {
        this.provider = provider;
        return this;
    }

    public World getWorld () {
        return world;
    }

    public void setWorld (World world) {
        this.world = world;
    }

    public BlockWithEntity getBlock () {
        return this.block;
    }

    public BlockEntity setBlock (BlockWithEntity block) {
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

    public GridPoint2 getInteractGridSize () {
        return interactGridSize;
    }

    public void setInteractGridSize (GridPoint2 interactGridSize) {
        this.interactGridSize = interactGridSize;
    }

    public Backpack getBackpack () {
        if (this.inventory instanceof Backpack) return (Backpack) this.inventory;
        return null;
    }

    public Inventory getInventory () {
        return this.inventory;
    }

    public BlockEntity setInventory (Inventory inventory) {
        this.inventory = inventory;
        //更新槽位所指向的容器
        getSlots().forEach(s -> s.setInventory(inventory));
        return this;
    }

    public List<Slot> getSlots () {
        return this.slots;
    }

    public BlockEntity setSlots (List<Slot> slots) {
        this.slots = slots;
        return this;
    }
}

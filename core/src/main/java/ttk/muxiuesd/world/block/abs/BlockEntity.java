package ttk.muxiuesd.world.block.abs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.Inventory;
import ttk.muxiuesd.interfaces.Tickable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.interfaces.world.block.BlockDrawable;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.InteractResult;
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
public abstract class BlockEntity implements Updateable, Tickable, BlockDrawable {
    private World world;                    //方块实体所属的世界
    private Block block;                    //方块
    private BlockPos blockPos;              //方块实体的位置
    private GridPoint2 interactGridSize;    //方块实体的交互网格大小
    private Inventory inventory;            //方块实体所拥有的容器
    private List<Slot> slots;               //交互槽位

    public BlockEntity(World world, Block block, BlockPos blockPos, int inventorySize) {
        this(world, block, blockPos, new GridPoint2(16, 16), inventorySize);
    }

    public BlockEntity (World world, Block block, BlockPos blockPos, GridPoint2 interactGridSize, int inventorySize) {
        this.world = world;
        this.block = block;
        this.blockPos = blockPos;
        this.interactGridSize = interactGridSize;
        this.inventory = new Backpack(inventorySize);
        this.slots = new ArrayList<>();
    }

    /**
     * 空手与方块互动
     * */
    public InteractResult interact (World world, LivingEntity user, GridPoint2 interactGridPos) {
        return InteractResult.FAILURE;
    }

    /**
     * 手持物品与方块互动
     * */
    public InteractResult interactWithItem (World world, LivingEntity user, ItemStack handItemStack, GridPoint2 interactGridPos) {
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
        EntitySystem es = (EntitySystem) world.getSystemManager().getSystem("EntitySystem");
        //掉落物品
        for (int i = 0; i < this.getInventory().getSize(); i++) {
            ItemStack itemStack = this.getInventory().getItemStack(i);
            if (itemStack == null) continue;

            itemStack.getItem().beDropped(itemStack, world, destroyer);
            ItemEntity itemEntity = (ItemEntity) Gets.ENTITY(Fight.getId("item_entity"), es);
            itemEntity.setItemStack(itemStack);
            itemEntity.setPosition(getBlockPos());
            itemEntity.setSize(ItemEntity.DEFAULT_SIZE);
            itemEntity.setOnGround(false);
            itemEntity.setOnAirTimer(new TaskTimer(0.2f, 0, () -> itemEntity.setOnAirTimer(null)));
            float speed = MathUtils.random(2f, 3.5f);
            double radian = Util.randomRadian();
            itemEntity.setSpeed(speed);
            itemEntity.setVelocity(new Vector2((float) Math.cos(radian), (float) Math.sin(radian)));

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

    @Override
    public void draw (Batch batch, float x, float y) {
        //槽位不为空就渲染
        if (!this.slots.isEmpty()) this.drawAllSlots(batch, x, y);
    }

    /**
     * 绘制所有槽位
     * */
    public void drawAllSlots (Batch batch, float x, float y) {
        for (Slot slot: getSlots()) {
            if (slot.getItemStack() != null) {
                drawSlot(batch, slot, x, y);
            }
        }
    }

    /**
     * 绘制指定的槽位
     * */
    public void drawSlot (Batch batch, Slot slot, float x, float y) {
        GridPoint2 interactGridSize = getInteractGridSize();
        GridPoint2 startPos = slot.getStartPos();
        GridPoint2 size = slot.getSize();

        float slotX = x + (float) startPos.x / interactGridSize.x;
        float slotY = y + (float) startPos.y / interactGridSize.y;
        float slotWidth  = (float) size.x / interactGridSize.x;
        float slotHeight = (float) size.y / interactGridSize.y;
        batch.draw(slot.getItemStack().getItem().texture, slotX, slotY, slotWidth, slotHeight);
    }

    public World getWorld () {
        return world;
    }

    public void setWorld (World world) {
        this.world = world;
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

    public GridPoint2 getInteractGridSize () {
        return interactGridSize;
    }

    public void setInteractGridSize (GridPoint2 interactGridSize) {
        this.interactGridSize = interactGridSize;
    }

    public Inventory getInventory () {
        return inventory;
    }

    public BlockEntity setInventory (Inventory inventory) {
        this.inventory = inventory;
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

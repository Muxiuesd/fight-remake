package ttk.muxiuesd.world.entity.abs;

import com.badlogic.gdx.graphics.g2d.Batch;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.EntitiesReg;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.item.ItemPickUpState;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 活物实体
 * <p>
 * 有生命值、能持有、使用物品
 * <p>
 * TODO 活物实体能有buff影响其行为状态
 * */
public abstract class LivingEntity extends Entity {
    public float maxHealth; // 生命值上限
    public float curHealth; // 当前生命值
    public Backpack backpack;   //储存物品的背包
    private int handIndex;  //手部物品索引

    public void initialize (Group group, float maxHealth, float curHealth) {
        initialize(group, maxHealth, curHealth, 16);
    }
    public void initialize (Group group, float maxHealth, float curHealth, int backpackSize) {
        super.initialize(group);
        this.maxHealth = maxHealth;
        this.curHealth = curHealth;
        this.backpack = new Backpack(backpackSize);
    }

    @Override
    public void update (float delta) {
        super.update(delta);
        this.backpack.update(delta);
    }

    @Override
    public void draw (Batch batch) {
        super.draw(batch);
        //如果手上有物品，则绘制手上的物品
        ItemStack itemStack = this.getHandItemStack();
        if (itemStack != null) {
            itemStack.getItem().drawOnHand(batch, this);
        }
    }

    /**
     * 实体使用手上的物品
     * @return 物品使用成功返回true，使用失败或者手上没有物品可供使用则返回false
     * */
    public boolean useItem (World world) {
        ItemStack itemStack = this.getHandItemStack();
        if (itemStack != null) {
            return itemStack.use(world, this);
        }
        return false;
    }

    /**
     * 丢弃物品
     * @return 丢弃成功返回true，丢弃失败返回false
     * */
    public boolean dropItem (int index, int amount) {
        ItemStack itemStack = this.backpack.dropItem(index, amount);
        if (itemStack == null) {
            return false;
        }

        ItemEntity itemEntity = (ItemEntity) EntitiesReg.get("item_entity");
        itemEntity.setPosition(getPosition());
        itemEntity.setSize(getSize());
        itemEntity.setItemStack(itemStack);
        getEntitySystem().add(itemEntity);

        return true;
    }

    /**
     * 捡起物品
     * @param itemStack 被捡起来的物品堆叠
     * @return 若捡起物品后返回的为null说明物品被全部捡起，返回WHOLE；若不为null但数量有变化则是被部分捡起为PARTIAL，反之则为FAILURE
     * */
    public ItemPickUpState pickUpItem (ItemStack itemStack) {
        int oldAmount = itemStack.getAmount();
        ItemStack pickedUpItem = this.backpack.pickUpItem(itemStack);
        if (pickedUpItem == null) {
            return ItemPickUpState.WHOLE;
        }
        if (pickedUpItem.getAmount() != oldAmount) {
            return ItemPickUpState.PARTIAL;
        }
        return ItemPickUpState.FAILURE;
    }

    public boolean isDeath () {
        return this.curHealth <= 0;
    }

    public ItemStack getHandItemStack () {
        return this.backpack.getItemStack(this.handIndex);
    }

    public int getHandIndex () {
        return this.handIndex;
    }

    public void setHandIndex (int handIndex) {
        if (handIndex >= 0 && handIndex < this.backpack.getSize()) {
            this.handIndex = handIndex;
        }
    }
}

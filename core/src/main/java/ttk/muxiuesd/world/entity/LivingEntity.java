package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import ttk.muxiuesd.world.World;
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
    public boolean useItem (World world, LivingEntity user) {
        ItemStack itemStack = this.getHandItemStack();
        if (itemStack != null) {
            return itemStack.getItem().ues(world, user);
        }
        return false;
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

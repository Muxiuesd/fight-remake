package ttk.muxiuesd.world.entity.abs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.registry.Entities;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.util.Timer;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.damage.DamageType;
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
    public static final float ATTACK_SPAN = 0.1f;   //受攻击状态维持时间
    public static final float SWING_HAND_TIME = 0.2f; //挥手一次所用的时间

    private float maxHealth; // 生命值上限
    private float curHealth; // 当前生命值
    private boolean attacked;   //是否收到攻击的状态
    private TaskTimer attackedTimer;    //被攻击状态持续的计时器
    public Backpack backpack;   //储存物品的背包
    private int handIndex;  //手部物品索引
    private Timer swingHandTimer;
    private float maxSwingHandDegree;


    public void initialize (Group group, float maxHealth, float curHealth) {
        initialize(group, maxHealth, curHealth, 16);
    }
    public void initialize (Group group, float maxHealth, float curHealth, int backpackSize) {
        super.initialize(group);
        this.maxHealth = maxHealth;
        this.curHealth = curHealth;
        this.attacked = false;
        this.attackedTimer = new TaskTimer(ATTACK_SPAN, 0f, () -> this.attacked = false);
        this.backpack = new Backpack(backpackSize);
        //this.swingHandTimer = new Timer(SWING_HAND_TIME, 0);
        this.maxSwingHandDegree = 60f;
    }

    @Override
    public void update (float delta) {
        super.update(delta);
        this.backpack.update(delta);
        this.attackedTimer.update(delta);
        this.attackedTimer.isReady();

        if (this.swingHandTimer != null) {
            this.swingHandTimer.update(delta);
            this.swingHandTimer.isReady();
        }
    }

    @Override
    public void draw (Batch batch) {
        //身体渲染
        if (!this.isAttacked()) {
            super.draw(batch);
        }else {
            // 受到攻击变红
            batch.setColor(255, 0, 0, 255);
            super.draw(batch);
            // 还原batch
            batch.setColor(255, 255, 255, 255);
        }

        //如果手上有物品，则绘制手上的物品
        ItemStack itemStack = this.getHandItemStack();
        if (itemStack != null) {
            itemStack.drawItemOnHand(batch, this);
        }
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        ItemStack itemStack = this.getHandItemStack();
        if (itemStack != null) {
            itemStack.renderShape(batch);
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
     * @return 丢弃成功返回丢出来的物品实体，丢弃失败返回null
     * */
    public ItemEntity dropItem (int index, int amount) {
        ItemStack itemStack = this.backpack.dropItem(index, amount);
        if (itemStack == null) return null;
        //简单的生成一个物品实体而已
        ItemEntity itemEntity = (ItemEntity) Gets.ENTITY(Entities.ITEM_ENTITY.getID(), getEntitySystem());
        itemEntity.setPosition(getPosition());
        itemEntity.setOnGround(false);
        itemEntity.setOnAirTimer(new TaskTimer(0.3f, 0, () -> itemEntity.setOnAirTimer(null)));
        itemEntity.setItemStack(itemStack);
        itemStack.getItem().beDropped(itemStack, getEntitySystem().getWorld(), this);

        return itemEntity;
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

    /**
     * 应用伤害
     * */
    public <S> void applyDamage (DamageType<S, LivingEntity> damageType, S source) {
        //TODO 各种判定
        damageType.apply(source, this);
        this.setAttacked(true);
    }

    public boolean isDeath () {
        return this.curHealth <= 0;
    }

    /**
     * 活物实体死亡执行
     * */
    public void onDeath (World world) {
        Backpack bp = this.getBackpack();
        for (int i = 0; i < bp.getSize(); i++) {
            ItemStack itemStack = bp.getItemStack(i);
            if (itemStack == null) continue;

            this.dropItem(i, itemStack.getAmount())
                .setLivingTime(Fight.ITEM_ENTITY_PICKUP_SPAN);
        }
    }

    public ItemStack getHandItemStack () {
        return this.backpack.getItemStack(this.handIndex);
    }

    public void setHandItemStack (ItemStack itemStack) {
        this.backpack.setItemStack(this.getHandIndex(), itemStack);
    }

    public int getHandIndex () {
        return this.handIndex;
    }

    public void setHandIndex (int handIndex) {
        if (handIndex >= 0 && handIndex < this.backpack.getSize()) {
            if (this.handIndex != handIndex) {
                //放下先前的物品堆叠
                ItemStack handItemStack = this.getHandItemStack();
                if (handItemStack != null) {
                    handItemStack.putDown(this.getEntitySystem().getWorld(), this);
                }
                this.handIndex = handIndex;
            }
        }
    }

    public void swingHand () {
        this.swingHand(SWING_HAND_TIME);
    }

    /**
     * 挥手
     **/
    public void swingHand (float swingTime) {
        if (this.swingHandTimer == null) {
            this.swingHandTimer = new TaskTimer(swingTime, 0, () -> this.swingHandTimer = null);
        }
    }

    /**
     * 如果在挥手状态，获取挥手角度
     * */
    public float getSwingHandDegreeOffset () {
        if (this.swingHandTimer == null) return 0f;

        float v = MathUtils.PI2 / this.swingHandTimer.getMaxSpan() * this.swingHandTimer.getCurSpan();
        return this.getMaxSwingHandDegree() * MathUtils.sin(v);
    }

    /**
     * 获取当前实体的朝向
     * */
    public Direction getDirection () {
        return new Direction(velX, velY);
    }

    /**
     * 减少一定的血量
     * */
    public LivingEntity decreaseHealth (float value) {
        this.setCurHealth(Math.max(this.getCurHealth() - value, 0f));
        return this;
    }

    /**
     * 增加一定的血量
     * */
    public LivingEntity increaseHealth (float value) {
        float after = this.getCurHealth() + value;
        this.setCurHealth(Math.min(after, this.getMaxHealth()));
        return this;
    }

    public float getMaxHealth () {
        return this.maxHealth;
    }

    public LivingEntity setMaxHealth (float maxHealth) {
        this.maxHealth = maxHealth;
        return this;
    }

    public float getCurHealth () {
        return this.curHealth;
    }

    public LivingEntity setCurHealth (float curHealth) {
        this.curHealth = curHealth;
        return this;
    }

    /**
     * 查询是否在受攻击状态
     * */
    public boolean isAttacked () {
        return this.attacked;
    }

    /**
     * 设置是否在受攻击状态
     * */
    public LivingEntity setAttacked (boolean attacked) {
        if (attacked) {
            this.attackedTimer.setCurSpan(0f);
        }else {
            this.attackedTimer.setCurSpan(ATTACK_SPAN);
        }
        this.attacked = attacked;
        return this;
    }


    public float getMaxSwingHandDegree () {
        return this.maxSwingHandDegree;
    }

    public Backpack getBackpack () {
        return this.backpack;
    }

    public LivingEntity setBackpack (Backpack backpack) {
        this.backpack = backpack;
        return this;
    }
}

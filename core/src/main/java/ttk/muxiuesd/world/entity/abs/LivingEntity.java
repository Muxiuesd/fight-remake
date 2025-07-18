package ttk.muxiuesd.world.entity.abs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.registry.Entities;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.entity.EntityType;
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
    public static final Vector2 DEFAULT_SIZE = Pools.VEC2.obtain().set(1f, 1f);
    public static final float ATTACK_SPAN = 0.1f;   //受攻击状态维持时间
    public static final float SWING_HAND_TIME = 0.2f; //挥手一次所用的时间

    private float maxHealth; // 生命值上限
    private float curHealth; // 当前生命值
    private boolean attacked;   //是否收到攻击的状态
    private TaskTimer attackedTimer;    //被攻击状态持续的计时器
    public Backpack backpack;   //储存物品的背包
    public boolean renderHandItem = false;  //是否渲染手部持有的物品（有的实体没有手，持有物品不用渲染出来）
    private int handIndex;  //手部物品索引
    private TaskTimer swingHandTimer;
    private float maxSwingHandDegree;


    public void initialize (EntityType<? extends LivingEntity> entityType, float maxHealth, float curHealth) {
        initialize(entityType, maxHealth, curHealth, 16);
    }
    public void initialize (EntityType<? extends LivingEntity> entityType, float maxHealth, float curHealth, int backpackSize) {
        super.initialize(entityType);
        setSize(DEFAULT_SIZE);
        this.maxHealth = maxHealth;
        this.curHealth = curHealth;
        this.attacked = false;
        this.attackedTimer = Pools.TASK_TIMER.obtain()
            .setMaxSpan(ATTACK_SPAN)
            .setCurSpan(0)
            .setTask(() -> this.attacked = false);
        this.backpack = new Backpack(backpackSize);
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
        if (this.renderHandItem) this.drawHandItem(batch);
    }

    /**
     * 手上持有的物品绘制
     * */
    public void drawHandItem (Batch batch) {
        //如果手上有物品，则绘制手上的物品
        ItemStack itemStack = this.getHandItemStack();
        if (itemStack != null) {
            itemStack.drawItemOnHand(batch, this);
        }
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        if (this.renderHandItem) this.renderShapeHandItem(batch);
    }

    /**
     * 持有物品的形状渲染
     * */
    public void renderShapeHandItem (ShapeRenderer batch) {
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
        //使得物品中心与实体中心对齐
        itemEntity.setPosition(getCenter().sub(itemEntity.getSize().scl(0.5f)));
        itemEntity.setOnGround(false);
        itemEntity.setOnAirTimer(Pools.TASK_TIMER.obtain().setMaxSpan(0.5f).setCurSpan(0)
            .setTask(() -> {
                Pools.TASK_TIMER.free(itemEntity.getOnAirTimer());
                itemEntity.setOnAirTimer(null);
        }));
        itemEntity.setItemStack(itemStack);
        itemStack.getItem().beDropped(itemStack, getEntitySystem().getWorld(), this);

        AudioPlayer.getInstance().playSound(Fight.getId("pop"));

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
        //死亡默认掉落所有物品
        Backpack bp = this.getBackpack();
        for (int i = 0; i < bp.getSize(); i++) {
            ItemStack itemStack = bp.getItemStack(i);
            if (itemStack == null) continue;

            //赋予随机方向的速度
            float radian = MathUtils.random() * MathUtils.PI2;
            float speed = MathUtils.random(1.3f, 2f);
            float velX = MathUtils.cos(radian);
            float velY = MathUtils.sin(radian);

            ItemEntity itemEntity = this.dropItem(i, itemStack.getAmount());
            itemEntity.setLivingTime(Fight.ITEM_ENTITY_PICKUP_SPAN);
            itemEntity.setSpeed(speed);
            itemEntity.setCurSpeed(speed);
            itemEntity.setVelocity(velX, velY);
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
            //this.swingHandTimer = new TaskTimer(swingTime, 0, () -> this.swingHandTimer = null);
            this.swingHandTimer = Pools.TASK_TIMER.obtain()
                .setMaxSpan(swingTime)
                .setCurSpan(0)
                .setTask(() -> {
                    Pools.TASK_TIMER.free(this.swingHandTimer);
                    this.swingHandTimer = null;
                });
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

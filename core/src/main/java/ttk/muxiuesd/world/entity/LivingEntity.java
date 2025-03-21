package ttk.muxiuesd.world.entity;

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

    public void initialize (Group group, float maxHealth, float curHealth) {
        initialize(group, maxHealth, curHealth, 16);
    }
    public void initialize (Group group, float maxHealth, float curHealth, int backpackSize) {
        super.initialize(group);
        this.maxHealth = maxHealth;
        this.curHealth = curHealth;
        this.backpack = new Backpack(backpackSize);
    }

    public boolean isDeath () {
        return this.curHealth <= 0;
    }
}

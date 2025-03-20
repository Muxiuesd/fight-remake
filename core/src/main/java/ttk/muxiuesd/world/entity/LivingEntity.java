package ttk.muxiuesd.world.entity;

/**
 * 活物实体
 * <p>
 * 有生命值之类的
 * <p>
 * TODO 活物实体能有buff影响其行为状态
 * */
public abstract class LivingEntity extends Entity {
    public float maxHealth; // 生命值上限
    public float curHealth; // 当前生命值

    public void initialize (Group group, float maxHealth, float curHealth) {
        super.initialize(group);
        this.maxHealth = maxHealth;
        this.curHealth = curHealth;
    }

    public boolean isDeath () {
        return this.curHealth <= 0;
    }
}

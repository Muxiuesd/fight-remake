package ttk.muxiuesd.world.entity.abs;

import game.muxiuesd.bedrockcore.util.TaskTimer;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterBulletShoot;
import ttk.muxiuesd.event.poster.EventPosterEntityHurt;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.registry.DamageTypes;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.bullet.BulletFire;
import ttk.muxiuesd.world.entity.player.Player;

/**
 * 敌人实体抽象类
 * <p>
 * 继承寻路实体，追击目标时通过 {@link PathFindingEntity#walkToTarget} 走流场寻路
 * <p>
 * 攻击模式：远程攻击 {@link #remoteAttack} 与近战攻击 {@link #meleeAttack}，
 * 通过统一入口 {@link #attack} 分发（默认远程，近战敌人覆写为近战攻击）
 * */
public abstract class Enemy<E extends Enemy<E>> extends PathFindingEntity<E> {
    public static final int MAX_RANDOM_COUNT = 5;

    /// 通用的敌人状态id（近战/远程敌人共用）
    public static final String STATE_REST = Fight.ID("rest");
    public static final String STATE_RANDOM_WALK = Fight.ID("random_walk");
    public static final String STATE_ATTACK_TARGET = Fight.ID("attack_target");

    private Entity<?> curTarget;    //敌人当前需要攻击的目标
    private TaskTimer attackTimer;  //攻击计时器
    private float visionRange;      //视野范围
    private float attackRange;      //攻击范围，再此范围内的会被锁定并攻击
    private float meleeDamage = 3f; //近战攻击的伤害值（远程攻击不适用）

    public Enemy (World world, EntityType<?> entityType) {
        this(world, entityType, 10, 10, 10, 5, 2, 3);
    }

    public Enemy (World world, EntityType<?> entityType,
                  float maxHealth, float curHealth,
                  float visionRange, float attackRange, float attackSpan, float speed) {
        super(world, entityType, maxHealth, curHealth);

        this.visionRange = visionRange;
        this.attackRange = attackRange;
        this.attackTimer = Pools.TASK_TIMER.obtain().setMaxSpan(attackSpan);

        setSpeed(speed);
        setSize(LivingEntity.DEFAULT_SIZE);
    }

    @Override
    public void update (float delta) {
        //先坐标更新，再更新其他的，否则实体移动速度有bug
        // positionChange 已移至 GroundEntityCollisionSystem 统一处理
        super.update(delta);
    }

    /**
     * 更新目标
     * */
    public void updateTarget () {
        this.updateTarget(getEntitySystem());
    }

    /**
     * 自定义敌人的目标更新逻辑
     * */
    public void updateTarget (EntitySystem es) {
        //默认以玩家为攻击目标
        Player player = es.getPlayer();
        if (player == null) return;
        //玩家死亡就跳过
        if (player.isDeath()) return;
        //需要在视野范围才跟踪玩家
        if (Util.getDistance(this, player) <= this.getVisionRange()) {
            this.setCurTarget(player);
            return;
        }
        this.setCurTarget(null);
    }

    /**
     * 远程攻击
     * */
    public void remoteAttack (float delta) {
        this.remoteAttack(delta, getEntitySystem());
    }
    /**
     * 远程攻击行为
     * */
    public void remoteAttack (float delta, EntitySystem es) {
        Entity<?> target = this.checkAttackAvailable(delta);
        if (target == null) return;
        //在攻击范围之内且攻击间隔到了就要攻击
        Bullet<?> bullet = this.createBullet(
            this,
            new Direction(target.getX() - getX(), target.getY() - getY())
        );
        es.add(bullet);
        EventBus.post(EventTypes.BULLET_SHOOT, new EventPosterBulletShoot(es.getWorld(), this, bullet));
    }

    /**
     * 近战攻击
     * */
    public void meleeAttack (float delta) {
        this.meleeAttack(delta, getEntitySystem());
    }
    /**
     * 近战攻击行为：攻击范围内的目标直接造成近战伤害
     * */
    public void meleeAttack (float delta, EntitySystem es) {
        Entity<?> target = this.checkAttackAvailable(delta);
        if (target == null) return;
        //只有活物才能被攻击
        if (!(target instanceof LivingEntity<?> livingTarget)) return;
        livingTarget.applyDamage(DamageTypes.MELEE, this);
        //发送事件
        EventBus.post(EventTypes.ENTITY_HURT, new EventPosterEntityHurt(es.getWorld(), this, livingTarget));
    }

    /**
     * 统一的攻击入口
     * <p>
     * 默认远程攻击，近战敌人（如僵尸）覆写为近战攻击
     * */
    public void attack (float delta) {
        this.attack(delta, getEntitySystem());
    }
    public void attack (float delta, EntitySystem es) {
        this.remoteAttack(delta, es);
    }

    /**
     * 检查攻击前置条件并返回可攻击的目标
     * <p>
     * 无目标/目标已死亡/超出攻击范围/攻击间隔未到 返回 null
     * */
    private Entity<?> checkAttackAvailable (float delta) {
        this.attackTimer.update(delta);

        Entity<?> target = this.getCurTarget();
        //无目标或目标已死亡不攻击
        if (target == null || (target instanceof LivingEntity<?> livingEntity && livingEntity.isDeath())) {
            return null;
        }
        float distance = Util.getDistance(this, target);
        //在攻击范围之外不攻击
        if (distance > this.getAttackRange()) {
            return null;
        }
        //攻击间隔没到就不攻击
        if (!this.attackTimer.isReady()) {
            return null;
        }
        return target;
    }

    /**
     * 自定义发射的子弹
     * @param direction 子弹的运动方向
     * */
    public Bullet<?> createBullet (Entity<?> owner, Direction direction) {
        BulletFire bullet = (BulletFire) Gets.BULLET(Fight.ID("bullet_fire"), owner.getEntitySystem());
        bullet.setOwner(owner);
        bullet.setSize(0.5f, 0.5f);
        //实体坐标与子弹坐标都是中心坐标，直接对齐发射
        bullet.setPosition(getX(), getY());
        bullet.setVelocity(direction, bullet.getSpeed());
        return bullet;
    }


    public Entity<?> getCurTarget () {
        return this.curTarget;
    }

    public Enemy<?> setCurTarget (Entity<?> curTarget) {
        this.curTarget = curTarget;
        return this;
    }

    /**
     * 是否有攻击目标
     * */
    public boolean hasTarget () {
        if (this.curTarget == null) return false;

        if (this.curTarget instanceof LivingEntity<?> livingEntityTarget) {
            return !livingEntityTarget.isDeath();
        }

        return true;
    }

    /**
     * 更新目标，再检查目标是否存在
     * */
    public boolean checkTarget () {
        this.updateTarget();
        return this.hasTarget();
    }

    public float getVisionRange () {
        return visionRange;
    }

    public Enemy<?> setVisionRange (float visionRange) {
        this.visionRange = visionRange;
        return this;
    }

    public float getAttackRange () {
        return attackRange;
    }

    public Enemy<?> setAttackRange (float attackRange) {
        this.attackRange = attackRange;
        return this;
    }

    /**
     * 获取近战攻击的伤害值
     * */
    public float getMeleeDamage () {
        return meleeDamage;
    }

    /**
     * 设置近战攻击的伤害值
     * */
    public Enemy<?> setMeleeDamage (float meleeDamage) {
        this.meleeDamage = meleeDamage;
        return this;
    }

    @Override
    public void dispose () {
        super.dispose();
        if (this.attackTimer != null) {
            Pools.TASK_TIMER.free(this.attackTimer);
            this.attackTimer = null;
        }
    }
}

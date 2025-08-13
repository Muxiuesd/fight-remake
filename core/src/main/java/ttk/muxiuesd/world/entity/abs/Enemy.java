package ttk.muxiuesd.world.entity.abs;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterBulletShoot;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.bullet.BulletFire;

/**
 * 敌人实体抽象类
 * */
public abstract class Enemy<E extends Enemy<?>> extends LivingEntity<E> {
    public static final int MAX_RANDOM_COUNT = 5;

    private Entity<?> curTarget;   //敌人当前需要攻击的目标
    private TaskTimer attackTimer;  //攻击计时器
    private float visionRange;  //视野范围
    private float attackRange;  //攻击范围，再此范围内的会被锁定并攻击

    public Enemy (World world, EntityType<?> entityType,
                  String textureId, float maxHealth, float curHealth,
                  float visionRange, float attackRange, float attackSpan, float speed) {
        this(world, entityType, maxHealth, curHealth, visionRange, attackRange, attackSpan, speed);
        this.loadBodyTextureRegion(textureId, null);
    }

    public Enemy (World world, EntityType<?> entityType) {
        this(world, entityType, 10, 10, 10, 5, 2, 3);
    }

    public Enemy (World world, EntityType<?> entityType,
                  float maxHealth, float curHealth,
                  float visionRange, float attackRange, float attackSpan, float speed) {
        super(world, entityType, maxHealth, curHealth);

        this.visionRange = visionRange;
        this.attackRange = attackRange;
        this.speed = speed;
        this.attackTimer = Pools.TASK_TIMER.obtain().setMaxSpan(attackSpan);

        setSize(1f, 1f);
    }

    @Override
    public void update (float delta) {
        /*//先更新目标
        this.updateTarget(delta, getEntitySystem());
        //更新位置
        this.updatePosition(delta);
        //当前目标不是null就执行攻击方法
        if (this.getCurTarget() != null) {
            this.attack(delta, getEntitySystem());
        }*/

        super.update(delta);
    }

    /**
     * 更新位置，一般朝向目标方向移动
     * */
    public void updatePosition (float delta) {

        //TODO 敌人没有目标时随意游走
    }

    /**
     * 朝向目标走去
     * */
    public void walkToTarget (float delta) {
        Entity<?> target = this.getCurTarget();
        Direction direction = new Direction(target.x - x, target.y - y);
        setVelocity(direction.getxDirection() * curSpeed, direction.getyDirection() * curSpeed);
        this.x += velX * delta;
        this.y += velY * delta;
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
        this.attackTimer.update(delta);

        Entity<?> target = this.getCurTarget();
        float distance = Util.getDistance(this, target);
        //在攻击范围之外不攻击
        if (distance > getAttackRange()) {
            return;
        }
        //攻击间隔没到就不攻击
        if (!this.attackTimer.isReady()) {
            return;
        }
        //在攻击范围之内且攻击间隔到了就要攻击
        Bullet bullet = this.createBullet(this, new Direction(target.x - x, target.y - y));
        es.add(bullet);
        EventBus.post(EventTypes.BULLET_SHOOT, new EventPosterBulletShoot(es.getWorld(), this, bullet));
    }

    /**
     * 自定义发射的子弹
     * @param direction 子弹的运动方向
     * */
    public Bullet createBullet (Entity<?> owner, Direction direction) {
        BulletFire bullet = (BulletFire) Gets.BULLET(Fight.getId("bullet_fire"), owner.getEntitySystem());
        bullet.setOwner(owner);
        bullet.setSize(0.5f, 0.5f);
        bullet.setPosition(x + (getWidth() - bullet.getWidth())/2, y + (getHeight() - bullet.getHeight())/2);
        bullet.setDirection(direction.getxDirection(), direction.getyDirection());
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

    @Override
    public void dispose () {
        super.dispose();
        if (this.attackTimer != null) {
            Pools.TASK_TIMER.free(this.attackTimer);
            this.attackTimer = null;
        }
    }
}

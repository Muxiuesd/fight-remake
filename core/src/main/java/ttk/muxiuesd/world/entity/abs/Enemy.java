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
        //先更新目标
        this.updateTarget(delta, getEntitySystem());
        //更新位置
        this.updatePosition(delta);
        //当前目标不是null就执行攻击方法
        if (this.getCurTarget() != null) {
            this.attack(delta, getEntitySystem());
        }
        super.update(delta);
    }

    /**
     * 更新位置，一般朝向目标方向移动
     * */
    public void updatePosition (float delta) {
        Entity<?> target = getCurTarget();
        if (target != null) {
            Direction direction = new Direction(target.x - x, target.y - y);
            setVelocity(direction.getxDirection() * curSpeed * delta, direction.getyDirection() * curSpeed * delta);
            this.x += velX;
            this.y += velY;
        }
        //TODO 敌人没有目标时随意游走
    }

    /**
     * 自定义敌人的目标更新逻辑
     * */
    public void updateTarget (float delta, EntitySystem es) {
        //默认以玩家为攻击目标
        Player player = es.getPlayer();
        //需要在视野范围才跟踪玩家
        if (player != null && Util.getDistance(this, player) <= this.getVisionRange()) {
            this.setCurTarget(player);
            return;
        }
        this.setCurTarget(null);
    }

    /**
     * 攻击行为
     * */
    public void attack (float delta, EntitySystem es) {
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
        getEntitySystem().add(bullet);
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
        return curTarget;
    }

    public Enemy<?> setCurTarget (Entity<?> curTarget) {
        this.curTarget = curTarget;
        return this;
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

package ttk.muxiuesd.world.entity.abs;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.system.GroundEntitySystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Timer;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.bullet.BulletFire;

/**
 * 敌人实体抽象类
 * */
public abstract class Enemy extends LivingEntity {
    private Entity curTarget;   //敌人当前需要攻击的目标
    private Timer attackTimer;
    private float visionRange;  //视野范围
    private float attackRange;  //攻击范围，再此范围内的会被锁定并攻击

    public Enemy (String textureId, float maxHealth, float curHealth,
                  float visionRange, float attackRange, float attackSpan, float speed) {
        this(maxHealth, curHealth, visionRange, attackRange, attackSpan, speed);
        this.loadBodyTextureRegion(textureId, null);
    }

    public Enemy (float maxHealth, float curHealth,
                  float visionRange, float attackRange, float attackSpan, float speed) {
        initialize(Group.enemy, maxHealth, curHealth);

        this.visionRange = visionRange;
        this.attackRange = attackRange;
        this.speed = speed;
        this.attackTimer = new Timer(attackSpan);

        setSize(1f, 1f);
    }

    @Override
    public void update (float delta) {
        //this.attackTimer.update(delta);
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
        Entity target = getCurTarget();
        if (target != null) {
            Direction direction = new Direction(target.x - x, target.y - y);
            this.x += direction.getxDirection() * curSpeed * delta;
            this.y += direction.getyDirection() * curSpeed * delta;
        }
        //TODO 敌人没有目标时随意游走
    }

    /**
     * 自定义敌人的目标更新逻辑
     * */
    public void updateTarget (float delta, GroundEntitySystem es) {
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
    public void attack (float delta, GroundEntitySystem es) {
        this.attackTimer.update(delta);

        Entity target = this.getCurTarget();
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
    }

    /**
     * 自定义发射的子弹
     * @param direction 子弹的运动方向
     * */
    public Bullet createBullet (Entity owner, Direction direction) {
        BulletFire bullet = (BulletFire) Gets.BULLET(Fight.getId("bullet_fire"));
        bullet.setOwner(owner);
        bullet.setSize(0.5f, 0.5f);
        bullet.setPosition(x + (getWidth() - bullet.getWidth())/2, y + (getHeight() - bullet.getHeight())/2);
        bullet.setDirection(direction.getxDirection(), direction.getyDirection());
        return bullet;
    }

    /**
     * 加载身体材质
     * */
    public void loadBodyTextureRegion (String textureId, String texturePath) {
        bodyTexture = this.getTextureRegion(textureId, texturePath);
    }

    public Entity getCurTarget () {
        return curTarget;
    }

    public Enemy setCurTarget (Entity curTarget) {
        this.curTarget = curTarget;
        return this;
    }

    public float getVisionRange () {
        return visionRange;
    }

    public Enemy setVisionRange (float visionRange) {
        this.visionRange = visionRange;
        return this;
    }

    public float getAttackRange () {
        return attackRange;
    }

    public Enemy setAttackRange (float attackRange) {
        this.attackRange = attackRange;
        return this;
    }
}

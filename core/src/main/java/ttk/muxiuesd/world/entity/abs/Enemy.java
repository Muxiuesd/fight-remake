package ttk.muxiuesd.world.entity.abs;

import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.Player;

/**
 * 敌人实体抽象类
 * */
public abstract class Enemy extends LivingEntity {
    private Entity curTarget;   //敌人当前需要攻击的目标

    private float visionRange;  //视野范围
    private float attackRange;  //攻击范围，再此范围内的会被锁定并攻击
    private float attackSpan;   //攻击时间间隔
    private float span = 0f;


    public Enemy (String textureId,float maxHealth, float curHealth,
                  float visionRange, float attackRange, float attackSpan, float speed) {
        this(maxHealth, curHealth, visionRange, attackRange, attackSpan, speed);
        this.loadBodyTextureRegion(textureId, null);
    }

    public Enemy (float maxHealth, float curHealth,
                  float visionRange, float attackRange, float attackSpan, float speed) {
        initialize(Group.enemy, maxHealth, curHealth);

        this.visionRange = visionRange;
        this.attackRange = attackRange;
        this.attackSpan = attackSpan;
        this.speed = speed;

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
        Entity target = getCurTarget();
        if (target != null) {
            Direction direction = new Direction(target.x - x, target.y - y);
            this.x = x + direction.getxDirection() * speed * delta;
            this.y = y + direction.getyDirection() * speed * delta;
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
        this.span += delta;
        //攻击间隔没到就不攻击
        if (this.span <= this.getAttackSpan()) {
            return;
        }
        Entity target = this.getCurTarget();
        float distance = Util.getDistance(this, target);
        //在攻击范围之外不攻击
        if (distance > getAttackRange()) {
            return;
        }
        //在攻击范围内就要攻击
        Bullet bullet = this.createBullet(this, new Direction(target.x - x, target.y - y));
        getEntitySystem().add(bullet);
        this.span = 0;
    }

    /**
     * 自定义发射的子弹
     * @param direction 子弹的运动方向
     * */
    public abstract Bullet createBullet (Entity owner, Direction direction);

    /**
     * 加载身体材质
     * */
    public void loadBodyTextureRegion (String textureId, String texturePath) {
        textureRegion = this.loadTextureRegion(textureId, texturePath);
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

    public float getAttackSpan () {
        return attackSpan;
    }

    public Enemy setAttackSpan (float attackSpan) {
        this.attackSpan = attackSpan;
        return this;
    }
}

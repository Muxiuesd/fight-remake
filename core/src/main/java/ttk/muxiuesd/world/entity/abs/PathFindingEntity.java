package ttk.muxiuesd.world.entity.abs;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.util.TaskTimer;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.system.PathfindingSystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;

/**
 * 有寻路能力的活物实体
 * <p>
 * 敌人实体、生物实体等需要用到寻路逻辑的实体都继承此类。
 * 寻路基于流场（Flow Field）：大量实体共享一个以玩家为目标的流场，
 * 每帧 O(1) 查询移动方向，性能远高于逐实体 A*。
 * <p>
 * 寻路实体有不同的本能行为：
 * <ul>
 *   <li>本能靠近目标：{@link #walkToTarget}（如史莱姆追玩家，受击不会逃跑）</li>
 *   <li>本能与目标保持距离：{@link #keepDistance}（如远程攻击实体、部分生物）</li>
 *   <li>被动型实体：受击后远离攻击者（需调用 {@link #setFleeWhenHurt} 开启）</li>
 * </ul>
 */
public abstract class PathFindingEntity<T extends PathFindingEntity<T>> extends LivingEntity<T> {
    /// 受击后本能逃跑的持续时间（秒）
    public static final float FLEE_DURATION = 1f;
    /// 卡住判定：多久没有实际位移视为卡住（秒）
    public static final float STUCK_TIME_THRESHOLD = 0.5f;
    /// 脱困持续时间（秒）
    public static final float UNSTUCK_DURATION = 0.3f;
    /// 脱困时方向偏移的角度（度），交替 ± 该角度试探
    public static final float UNSTUCK_ANGLE = 45f;
    /// 位移采样间隔（秒）
    public static final float MOVE_SAMPLE_SPAN = 0.1f;
    /// 采样窗口内位移小于该值视为"没有移动"（世界单位）
    public static final float MOVE_EPSILON = 0.05f;

    private Entity<?> fleeTarget;       //受击后要远离的目标（攻击者）
    private final TaskTimer fleeTimer;  //逃跑计时器
    private boolean fleeWhenHurt;       //是否开启"受击逃跑"本能（默认关闭，被动型实体需要时显式开启）

    /// 卡住检测
    private final Vector2 lastSamplePos = new Vector2();   //上次位移采样的位置
    private float sampleTimer;          //位移采样计时
    private float stuckTime;            //累计卡住时间
    private float unstuckTimer;         //脱困进行时间
    private boolean unstucking;         //是否正在脱困

    public PathFindingEntity (World world, EntityType<?> entityType) {
        super(world, entityType);
        this.fleeTimer = Pools.TASK_TIMER.obtain()
            .setMaxSpan(FLEE_DURATION)
            .setCurSpan(FLEE_DURATION);
    }
    public PathFindingEntity (World world, EntityType<?> entityType, float maxHealth, float curHealth) {
        super(world, entityType, maxHealth, curHealth);
        this.fleeTimer = Pools.TASK_TIMER.obtain()
            .setMaxSpan(FLEE_DURATION)
            .setCurSpan(FLEE_DURATION);
    }
    public PathFindingEntity (World world, EntityType<?> entityType, float maxHealth, float curHealth, int backpackSize) {
        super(world, entityType, maxHealth, curHealth, backpackSize);
        this.fleeTimer = Pools.TASK_TIMER.obtain()
            .setMaxSpan(FLEE_DURATION)
            .setCurSpan(FLEE_DURATION);
    }

    @Override
    public void update (float delta) {
        super.update(delta);

        //受击逃跑本能：逃跑期间持续远离攻击者
        if (this.fleeTarget != null) {
            this.fleeTimer.update(delta);
            if (this.fleeTimer.isReady()) {
                //逃跑结束
                this.fleeTarget = null;
            } else {
                this.fleeFrom(this.fleeTarget);
            }
        }

        //卡住检测与脱困：速度非零但长时间没有实际位移（撞墙/被挤/偏离路径）时试探偏移方向
        this.updateStuck(delta);
    }

    /**
     * 卡住检测与脱困
     * <p>
     * 周期性采样实体位置，若速度非零但位移几乎为零且持续超过阈值，则判定卡住；
     * 卡住期间沿流场方向偏移 ±{@link #UNSTUCK_ANGLE} 试探移动，直到恢复位移。
     */
    private void updateStuck (float delta) {
        //只有正在移动的实体才需要检测卡住
        if (this.getCurSpeed() <= 0.01f) {
            this.stuckTime = 0;
            this.unstuckTimer = 0;
            this.unstucking = false;
            return;
        }

        Vector2 pos = this.getPosition();
        this.sampleTimer += delta;
        if (this.sampleTimer >= MOVE_SAMPLE_SPAN) {
            this.sampleTimer = 0;
            float moved = Vector2.dst(pos.x, pos.y, this.lastSamplePos.x, this.lastSamplePos.y);
            if (moved < MOVE_EPSILON) {
                this.stuckTime += MOVE_SAMPLE_SPAN;
            } else {
                this.stuckTime = 0;
                this.unstuckTimer = 0;
                this.unstucking = false;
            }
            this.lastSamplePos.set(pos);
        }

        //卡住：开始脱困
        if (this.stuckTime >= STUCK_TIME_THRESHOLD) {
            this.unstucking = true;
        }

        if (this.unstucking) {
            this.unstuckTimer += delta;
            //脱困结束：重置卡住计时，重新采样（若仍卡住会再次触发脱困）
            if (this.unstuckTimer >= UNSTUCK_DURATION) {
                this.unstuckTimer = 0;
                this.stuckTime = 0;
                this.unstucking = false;
                this.lastSamplePos.set(pos);
                return;
            }

            //沿流场方向偏移 ±45°，每半个脱困周期交替方向，试探绕开障碍
            Direction baseDir = this.getFlowDirection();
            if (baseDir == null) {
                //流场不可用，保持当前速度方向并轻微偏移
                baseDir = new Direction(this.getVelX(), this.getVelY());
            }
            float sign = ((int) (this.unstuckTimer / (UNSTUCK_DURATION / 2f))) % 2 == 0 ? 1f : -1f;
            float rad = sign * UNSTUCK_ANGLE * MathUtils.degreesToRadians;
            float cos = MathUtils.cos(rad);
            float sin = MathUtils.sin(rad);
            float x = baseDir.getX() * cos - baseDir.getY() * sin;
            float y = baseDir.getX() * sin + baseDir.getY() * cos;
            this.setVelocity(x, y);
            this.setCurSpeed(this.getSpeed());
        }
    }

    /**
     * 获取实体的碰撞箱半径（世界单位，宽高最大值的一半）
     * <p>
     * 用于流场的障碍膨胀：路径会保证实体中心距离障碍 >= 该半径，碰撞箱不碰墙
     */
    public float getPathfindingRadius () {
        return Math.max(this.getWidth(), this.getHeight()) / 2f;
    }

    /**
     * 获取寻路系统
     */
    protected PathfindingSystem getPathfindingSystem () {
        if (this.getEntitySystem() == null) return null;
        return this.getEntitySystem().getWorld().getSystem(PathfindingSystem.class);
    }

    /**
     * 获取流场给出的移动方向
     * @return 可寻路返回方向；不可达（被墙围死/场外/流场未生成）返回 null
     */
    public Direction getFlowDirection () {
        PathfindingSystem pathfindingSystem = this.getPathfindingSystem();
        if (pathfindingSystem == null) return null;

        Vector2 pos = this.getCenterPos();
        return pathfindingSystem.getFlowDirection(pos.x, pos.y);
    }

    /**
     * 本能靠近目标（如史莱姆追玩家）
     * <p>
     * 优先走流场寻路，流场不可用时回退直线走向目标
     * @param target 要走向的目标实体
     */
    public void walkToTarget (Entity<?> target) {
        //无目标或目标死亡时不做任何移动
        if (target == null || (target instanceof LivingEntity<?> livingEntity && livingEntity.isDeath())) {
            this.setVelocity(0, 0);
            return;
        }

        //优先使用流场寻路：大量实体共享一个流场，O(1) 查询方向，性能高
        Direction flowDir = this.getFlowDirection();
        if (flowDir != null) {
            setVelocity(flowDir.getX(), flowDir.getY());
            setCurSpeed(getSpeed());
            return;
        }

        //回退：流场不可达（被墙围死/未生成）时保持原来的直线走向目标
        Direction direction = new Direction(target.getX() - getX(), target.getY() - getY());
        setVelocity(direction.getX(), direction.getY());
        setCurSpeed(getSpeed());
    }

    /**
     * 本能远离目标（如远程实体拉开距离、受击逃跑）
     * <p>
     * 优先走流场的远离方向（沿流场平滑绕墙），流场不可用时回退直线远离
     * @param target 要远离的目标实体
     */
    public void fleeFrom (Entity<?> target) {
        //无目标或目标死亡时不做任何移动
        if (target == null || (target instanceof LivingEntity<?> livingEntity && livingEntity.isDeath())) {
            this.setVelocity(0, 0);
            return;
        }

        //优先使用流场的远离方向（指向代价最大的可达邻居，即远离流场目标）
        Direction awayDir = this.getFlowAwayDirection();
        if (awayDir != null) {
            setVelocity(awayDir.getX(), awayDir.getY());
            setCurSpeed(getSpeed());
            return;
        }

        //回退：直线远离目标
        Direction direction = new Direction(getX() - target.getX(), getY() - target.getY());
        setVelocity(direction.getX(), direction.getY());
        setCurSpeed(getSpeed());
    }

    /**
     * 本能与目标保持一定距离（如远程攻击实体）
     * <p>
     * 太近就远离，太远就靠近，在目标距离范围内则原地不动
     * @param target  目标实体
     * @param minDist 与目标保持的最小距离
     * @param maxDist 与目标保持的最大距离
     */
    public void keepDistance (Entity<?> target, float minDist, float maxDist) {
        //无目标或目标死亡时不做任何移动
        if (target == null || (target instanceof LivingEntity<?> livingEntity && livingEntity.isDeath())) {
            this.setVelocity(0, 0);
            return;
        }

        float distance = Util.getDistance(this, target);
        if (distance < minDist) {
            //太近，远离
            this.fleeFrom(target);
        } else if (distance > maxDist) {
            //太远，靠近
            this.walkToTarget(target);
        } else {
            //距离合适，原地不动
            this.setVelocity(0, 0);
        }
    }

    /**
     * 获取流场给出的远离方向
     * @return 可远离返回方向；不可远离返回 null
     */
    public Direction getFlowAwayDirection () {
        PathfindingSystem pathfindingSystem = this.getPathfindingSystem();
        if (pathfindingSystem == null) return null;

        Vector2 pos = this.getCenterPos();
        return pathfindingSystem.getFlowAwayDirection(pos.x, pos.y);
    }

    /**
     * 当前是否正处于受击逃跑状态
     */
    public boolean isFleeing () {
        return this.fleeTarget != null;
    }

    /**
     * 是否开启"受击逃跑"本能
     * <p>
     * 默认关闭：敌人等攻击型实体受击后应继续战斗，不逃跑；
     * 被动型实体（受击后本能远离攻击者）需要显式开启
     */
    public boolean isFleeWhenHurt () {
        return this.fleeWhenHurt;
    }

    public T setFleeWhenHurt (boolean fleeWhenHurt) {
        this.fleeWhenHurt = fleeWhenHurt;
        return (T) this;
    }

    @Override
    public <S> void applyDamage (DamageType<S, LivingEntity<?>> damageType, S source) {
        super.applyDamage(damageType, source);
        //受击逃跑本能：仅开启了该本能的实体（被动型）受击后远离攻击者
        if (this.fleeWhenHurt && source instanceof Entity<?> attacker && !this.isDeath()) {
            this.fleeTarget = attacker;
            this.fleeTimer.setCurSpan(0);
        }
    }

    @Override
    public void dispose () {
        super.dispose();
        Pools.TASK_TIMER.free(this.fleeTimer);
    }
}

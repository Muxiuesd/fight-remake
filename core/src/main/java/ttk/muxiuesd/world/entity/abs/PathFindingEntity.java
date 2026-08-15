package ttk.muxiuesd.world.entity.abs;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.system.PathfindingSystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;

/**
 * 有寻路能力的活物实体
 * <p>
 * 敌人实体、生物实体等需要用到寻路逻辑的实体都继承此类。
 * 寻路基于流场（Flow Field）：大量实体共享一个以玩家为目标的流场，
 * 每帧 O(1) 查询移动方向，性能远高于逐实体 A*。
 */
public abstract class PathFindingEntity<T extends PathFindingEntity<T>> extends LivingEntity<T> {

    public PathFindingEntity (World world, EntityType<?> entityType) {
        super(world, entityType);
    }
    public PathFindingEntity (World world, EntityType<?> entityType, float maxHealth, float curHealth) {
        super(world, entityType, maxHealth, curHealth);
    }
    public PathFindingEntity (World world, EntityType<?> entityType, float maxHealth, float curHealth, int backpackSize) {
        super(world, entityType, maxHealth, curHealth, backpackSize);
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
     * 朝目标走去（优先走流场寻路，流场不可用时回退直线走向目标）
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
}

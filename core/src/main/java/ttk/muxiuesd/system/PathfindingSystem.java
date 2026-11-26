package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.util.TaskTimer;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.PathFindingEntity;
import ttk.muxiuesd.world.entity.pathfinding.AStarPathfinder;
import ttk.muxiuesd.world.entity.pathfinding.FlowField;
import ttk.muxiuesd.world.entity.pathfinding.PathWay;
import ttk.muxiuesd.world.entity.player.Player;

/**
 * 寻路系统
 * <p>
 * 基于流场（Flow Field）：大量实体追同一个目标（玩家）时，
 * 一次构建流场全员共享，实体每帧 O(1) 查询移动方向。
 * <p>
 * 流场重建触发：
 * 1. 周期性重建（{@link #REBUILD_SPAN}）
 * 2. 目标移动超过阈值（{@link #TARGET_MOVE_THRESHOLD}）
 * 3. 墙体变更（{@link #onWallChanged} 标记脏）
 */
public class PathfindingSystem extends WorldSystem {
    /// 流场重建周期（秒）
    public static final float REBUILD_SPAN = 0.4f;
    /// 目标移动多少格后触发重建
    public static final float TARGET_MOVE_THRESHOLD = 1f;

    public final String TAG = this.getClass().getName();

    //调试：是否渲染流场（不可走格子）
    public boolean flowFieldRender = false;

    private final FlowField flowField = new FlowField();
    private final TaskTimer rebuildTimer;
    private boolean dirty = false;      //墙体变更等导致的脏标记

    private final ChunkSystem cs;
    private EntitySystem es;        //延迟到 initialize 获取（EntitySystem 注册在 PathfindingSystem 之后）

    /// A* 寻路服务（供实体走独立目标用）
    private final AStarPathfinder aStarPathfinder = new AStarPathfinder();
    /// 每帧 A* 计算预算（防止大量实体同时寻路导致卡帧）
    private static final int A_STAR_BUDGET_PER_FRAME = 4;
    private int aStarUsedThisFrame = 0;    //本帧已使用的 A* 计算次数
    private long wallVersion = 0;          //墙体版本号（墙体变更时 +1，供 A* 路径失效判断）

    public PathfindingSystem (World world) {
        super(world);
        this.cs = world.getSystem(ChunkSystem.class);
        this.rebuildTimer = Pools.TASK_TIMER.obtain()
            .setMaxSpan(REBUILD_SPAN)
            .setCurSpan(REBUILD_SPAN)   //开局立即构建一次
            .setTask(this::rebuildFlowField);
    }

    @Override
    public void initialize () {
        this.es = getWorld().getSystem(EntitySystem.class);
    }

    @Override
    public void update (float delta) {
        //每帧重置 A* 预算
        this.aStarUsedThisFrame = 0;

        if (this.es == null) return;
        Player player = this.es.getPlayer();
        if (player == null) return;

        //目标移动超过阈值触发重建（横向或纵向移动超过 1 格即重建）
        Vector2 targetPos = player.getCenterPos();
        if (this.lastTargetPos != null) {
            float dx = Math.abs(targetPos.x - this.lastTargetPos.x);
            float dy = Math.abs(targetPos.y - this.lastTargetPos.y);
            if (dx >= TARGET_MOVE_THRESHOLD || dy >= TARGET_MOVE_THRESHOLD) {
                this.dirty = true;
            }
        }

        //脏标记立即重建，否则按周期重建
        if (this.dirty) {
            this.dirty = false;
            this.rebuildFlowField();
        } else {
            this.rebuildTimer.update(delta);
            this.rebuildTimer.isReady();
        }
    }

    /**
     * 重新构建流场（以玩家为目标）
     * <p>
     * 流场按当前所有寻路实体中的最大碰撞箱半径做障碍膨胀，
     * 保证任意大小的实体沿流场移动时碰撞箱都不会碰墙
     */
    public void rebuildFlowField () {
        Player player = this.es.getPlayer();
        if (player == null) return;

        Vector2 center = player.getCenterPos();
        float maxRadius = 0f;
        for (Entity<?> entity : this.es.getEntities()) {
            if (entity instanceof PathFindingEntity<?> pathFindingEntity) {
                maxRadius = Math.max(maxRadius, pathFindingEntity.getPathfindingRadius());
            }
        }
        this.flowField.build(this.cs, center.x, center.y, maxRadius);
        this.lastTargetPos = center;
    }

    /**
     * 墙体变更通知（由 ChunkSystem 在放置/破坏墙体后调用）
     */
    public void onWallChanged (float wx, float wy) {
        //变更点不在当前流场范围内就不需要重建
        if (this.flowField.contains(wx, wy)) {
            this.dirty = true;
        }
        //A* 路径也会受墙体影响：版本号 +1，实体检测到版本变化后自动重算路径
        this.wallVersion++;
    }

    /**
     * 获取墙体版本号（墙体变更时 +1，用于 A* 路径失效判断）
     */
    public long getWallVersion () {
        return this.wallVersion;
    }

    /**
     * 获取某坐标的移动方向（基于流场）
     * @return 可寻路返回方向，否则返回 null（调用方回退原有逻辑）
     */
    public Direction getFlowDirection (float worldX, float worldY) {
        if (!this.flowField.contains(worldX, worldY)) return null;
        return this.flowField.getDirection(worldX, worldY);
    }

    /**
     * 获取某坐标的远离方向（指向代价最大的可达邻居，即远离流场目标）
     * @return 可远离返回方向，否则返回 null（调用方回退原有逻辑）
     */
    public Direction getFlowAwayDirection (float worldX, float worldY) {
        if (!this.flowField.contains(worldX, worldY)) return null;
        return this.flowField.getAwayDirection(worldX, worldY);
    }

    /**
     * 计算一条从起点到目标的 A* 路径（供实体走向独立目标使用）
     * <p>
     * 受每帧预算限制：预算耗尽时返回 null，调用方应在后续帧重试
     * @param startX 起点世界坐标
     * @param startY 起点世界坐标
     * @param targetX 目标世界坐标
     * @param targetY 目标世界坐标
     * @param entityRadius 实体碰撞箱半径（世界单位），用于障碍膨胀
     * @param canSwim 实体是否可游泳（可游泳则水方块可走）
     * @return 路径；不可达/超范围/预算耗尽返回 null
     */
    public PathWay findPath (float startX, float startY,
                             float targetX, float targetY,
                             float entityRadius, boolean canSwim) {
        //预算耗尽：本帧不再计算（调用方下帧重试）
        if (this.aStarUsedThisFrame >= A_STAR_BUDGET_PER_FRAME) return null;
        this.aStarUsedThisFrame++;
        return this.aStarPathfinder.findPath(
            this.cs,
            startX, startY,
            targetX, targetY,
            entityRadius, canSwim
        );
    }

    /**
     * 获取流场（调试用）
     */
    public FlowField getFlowField () {
        return this.flowField;
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        if (!this.flowFieldRender) return;

        //绘制不可走的格子（红色方块）
        batch.setColor(new Color(1f, 0f, 0f, 0.4f));
        FlowField field = this.flowField;
        var origin = field.getOrigin();
        byte[][] cost = field.getCost();
        byte[][] dirIndex = field.getDirIndex();
        for (int y = 0; y < FlowField.SIZE; y++) {
            for (int x = 0; x < FlowField.SIZE; x++) {
                //cost 可达但 dirIndex 为 -1 的格子说明是障碍或不可走（未被 BFS 扩散到）
                if (cost[y][x] < 0 && dirIndex[y][x] < 0) continue;
                if (dirIndex[y][x] < 0) {
                    batch.rect(origin.x + x, origin.y + y, 1f, 1f);
                }
            }
        }
        batch.setColor(Color.WHITE);
    }

    @Override
    public void dispose () {
        Pools.TASK_TIMER.free(this.rebuildTimer);
    }

    private Vector2 lastTargetPos;      //上次构建流场时目标的位置
}

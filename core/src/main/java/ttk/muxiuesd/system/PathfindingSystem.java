package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.util.TaskTimer;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.pathfinding.FlowField;
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
     */
    public void rebuildFlowField () {
        Player player = this.es.getPlayer();
        if (player == null) return;

        Vector2 center = player.getCenterPos();
        this.flowField.build(this.cs, center.x, center.y);
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

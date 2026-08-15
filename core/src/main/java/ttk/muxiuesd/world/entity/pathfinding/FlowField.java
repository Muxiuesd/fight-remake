package ttk.muxiuesd.world.entity.pathfinding;

import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.chunk.Chunk;

/**
 * 流场（Flow Field）
 * <p>
 * 从目标点反向扩散 BFS，生成积分场与向量场：
 * 每个格子记录"走到代价最小的邻居"的方向，实体查询自己所在格即可 O(1) 获取移动方向。
 * 适用于"大量实体追同一个目标"的场景，一次构建全员共享，性能远高于逐实体 A*。
 * <p>
 * 数据全部复用，构建过程无对象分配（BFS 队列用数组模拟）。
 * 可行走性查询使用构建时缓存的区块引用直接访问数组，避免 getBlock/getWall 的线性遍历区块列表开销。
 */
public class FlowField {
    /// 流场覆盖的区域大小（格子数）
    public static final int SIZE = 64;
    /// 流场覆盖的区块数量（SIZE=64 / 区块边长 16 = 4）
    private static final int CHUNK_COUNT = SIZE / Chunk.ChunkWidth;
    /// 未被访问的格子的代价标记
    private static final int UNREACHABLE = -1;
    /// 8 邻域的方向偏移（顺序：右、右上、上、左上、左、左下、下、右下）
    private static final int[][] DIR_OFFSETS = {
        {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}
    };
    /// 8 邻域的指向方向（与 DIR_OFFSETS 一一对应）
    private static final Direction[] DIRECTIONS = {
        new Direction(1, 0), new Direction(1, 1), new Direction(0, 1), new Direction(-1, 1),
        new Direction(-1, 0), new Direction(-1, -1), new Direction(0, -1), new Direction(1, -1)
    };

    /// 流场左下角的世界坐标（格子坐标）
    private final GridPoint2 origin = new GridPoint2();
    /// 每个格子的代价（从目标到该格的最短步数），UNREACHABLE 表示不可达
    private final byte[][] cost;
    /// 每个格子应该走向的方向索引（指向 8 邻域中代价最小的邻居，即 DIR_OFFSETS/DIRECTIONS 的下标），-1 表示不可达或未构建
    private final byte[][] dirIndex;

    // BFS 队列（复用，避免分配）
    private final int[] queueX;
    private final int[] queueY;
    /// 区块缓存：流场范围内（4×4 区块）的区块引用，构建期间复用
    private final Chunk[][] chunkCache = new Chunk[CHUNK_COUNT][CHUNK_COUNT];

    public FlowField () {
        this.cost = new byte[SIZE][SIZE];
        this.dirIndex = new byte[SIZE][SIZE];
        this.queueX = new int[SIZE * SIZE];
        this.queueY = new int[SIZE * SIZE];
    }

    /**
     * 构建流场
     * @param cs 区块系统（用于查询墙体）
     * @param targetWorldX 目标点的世界坐标（格子坐标）
     * @param targetWorldY 目标点的世界坐标（格子坐标）
     */
    public void build (ChunkSystem cs, float targetWorldX, float targetWorldY) {
        //流场左下角：目标点向负方向偏移 SIZE/2 格
        this.origin.set(
            (int) Math.floor(targetWorldX) - SIZE / 2,
            (int) Math.floor(targetWorldY) - SIZE / 2
        );

        //初始化
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                this.cost[y][x] = UNREACHABLE;
                this.dirIndex[y][x] = -1;
            }
        }

        //缓存流场范围内的区块引用（最多 4×4 个），构建期间避免反复线性遍历 activeChunks
        int baseChunkX = (int) Math.floor(this.origin.x / (float) Chunk.ChunkWidth);
        int baseChunkY = (int) Math.floor(this.origin.y / (float) Chunk.ChunkHeight);
        for (int cy = 0; cy < CHUNK_COUNT; cy++) {
            for (int cx = 0; cx < CHUNK_COUNT; cx++) {
                this.chunkCache[cy][cx] = cs.getChunk(baseChunkX + cx, baseChunkY + cy);
            }
        }

        //目标点必须在流场内（目标取整后位于中心附近，必然在内）
        int targetLocalX = (int) Math.floor(targetWorldX) - this.origin.x;
        int targetLocalY = (int) Math.floor(targetWorldY) - this.origin.y;
        if (targetLocalX < 0 || targetLocalX >= SIZE || targetLocalY < 0 || targetLocalY >= SIZE) {
            return;
        }

        //BFS：从目标点反向扩散
        int head = 0, tail = 0;
        this.cost[targetLocalY][targetLocalX] = 0;
        this.queueX[tail] = targetLocalX;
        this.queueY[tail] = targetLocalY;
        tail++;

        while (head < tail) {
            int x = this.queueX[head];
            int y = this.queueY[head];
            head++;
            int curCost = this.cost[y][x];

            for (int i = 0; i < DIR_OFFSETS.length; i++) {
                int nx = x + DIR_OFFSETS[i][0];
                int ny = y + DIR_OFFSETS[i][1];
                if (nx < 0 || nx >= SIZE || ny < 0 || ny >= SIZE) continue;
                if (this.cost[ny][nx] != UNREACHABLE) continue;

                int wx = this.origin.x + nx;
                int wy = this.origin.y + ny;
                //该格不可走（墙体/水等）
                if (!isWalkable(wx, wy)) continue;
                //对角线移动需要检查两个相邻格，防止穿角
                if (DIR_OFFSETS[i][0] != 0 && DIR_OFFSETS[i][1] != 0) {
                    if (!isWalkable(this.origin.x + x + DIR_OFFSETS[i][0], this.origin.y + y)
                        || !isWalkable(this.origin.x + x, this.origin.y + y + DIR_OFFSETS[i][1])) {
                        continue;
                    }
                }

                this.cost[ny][nx] = (byte) (curCost + 1);
                //记录从该格指向当前格的方向（即走向代价更小的邻居）
                this.dirIndex[ny][nx] = (byte) i;
                this.queueX[tail] = nx;
                this.queueY[tail] = ny;
                tail++;
            }
        }
    }

    /**
     * 判断某个世界坐标对应的格子是否可行走
     * <p>
     * 使用构建时缓存的区块引用直接访问数组，避免 getBlock/getWall 的线性遍历区块列表开销
     */
    private boolean isWalkable (int wx, int wy) {
        int chunkX = (int) Math.floor(wx / (float) Chunk.ChunkWidth) - (int) Math.floor(this.origin.x / (float) Chunk.ChunkWidth);
        int chunkY = (int) Math.floor(wy / (float) Chunk.ChunkHeight) - (int) Math.floor(this.origin.y / (float) Chunk.ChunkHeight);
        if (chunkX < 0 || chunkX >= CHUNK_COUNT || chunkY < 0 || chunkY >= CHUNK_COUNT) return false;
        Chunk chunk = this.chunkCache[chunkY][chunkX];
        //区块未加载：不可走（保守，避免规划进未知区域后卡死）
        if (chunk == null) return false;
        GridPoint2 local = Chunk.worldToChunk(wx, wy);
        if (chunk.getWall(local.x, local.y) != null) return false;
        var block = chunk.getBlock(local.x, local.y);
        //方块为 null（旧存档缺格）时视为不可走
        return block != null && block.getProperty().isWalkable();
    }

    /**
     * 查询某世界坐标的移动方向
     * @return 可行走且可达返回方向；不可达（被墙围死/场外/未构建）返回 null
     */
    public Direction getDirection (float worldX, float worldY) {
        int localX = (int) Math.floor(worldX) - this.origin.x;
        int localY = (int) Math.floor(worldY) - this.origin.y;
        if (localX < 0 || localX >= SIZE || localY < 0 || localY >= SIZE) return null;

        int dir = this.dirIndex[localY][localX];
        if (dir < 0) return null;
        return DIRECTIONS[dir];
    }

    /**
     * 判断某世界坐标是否在流场覆盖范围内
     */
    public boolean contains (float worldX, float worldY) {
        int localX = (int) Math.floor(worldX) - this.origin.x;
        int localY = (int) Math.floor(worldY) - this.origin.y;
        return localX >= 0 && localX < SIZE && localY >= 0 && localY < SIZE;
    }

    /**
     * 获取流场左下角世界坐标（调试用）
     */
    public GridPoint2 getOrigin () {
        return this.origin;
    }

    public byte[][] getCost () {
        return this.cost;
    }

    public byte[][] getDirIndex () {
        return this.dirIndex;
    }
}

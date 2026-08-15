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
    /// 膨胀后的可走性（考虑实体碰撞箱半径），true 表示实体中心可位于该格
    private final boolean[][] walkable;

    // BFS 队列（复用，避免分配）
    private final int[] queueX;
    private final int[] queueY;
    /// 区块缓存：流场范围内（4×4 区块）的区块引用，构建期间复用
    private final Chunk[][] chunkCache = new Chunk[CHUNK_COUNT][CHUNK_COUNT];
    /// 墙格坐标记录（用于障碍膨胀），复用避免分配
    private final int[] wallCellsX;
    private final int[] wallCellsY;

    public FlowField () {
        this.cost = new byte[SIZE][SIZE];
        this.dirIndex = new byte[SIZE][SIZE];
        this.walkable = new boolean[SIZE][SIZE];
        this.queueX = new int[SIZE * SIZE];
        this.queueY = new int[SIZE * SIZE];
        this.wallCellsX = new int[SIZE * SIZE];
        this.wallCellsY = new int[SIZE * SIZE];
    }

    /**
     * 构建流场
     * @param cs 区块系统（用于查询墙体）
     * @param targetWorldX 目标点的世界坐标（格子坐标）
     * @param targetWorldY 目标点的世界坐标（格子坐标）
     * @param entityRadius 实体的碰撞箱半径（世界单位，取宽高的最大值的一半），
     *                     用于障碍膨胀：距离墙小于实体半径的格子视为不可走，
     *                     保证实体中心移动时碰撞箱不碰墙
     */
    public void build (ChunkSystem cs, float targetWorldX, float targetWorldY, float entityRadius) {
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
                this.walkable[y][x] = false;
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

        //计算基础可走性，同时记录墙格（墙是唯一需要膨胀的障碍，水/未加载仅自身不可走）
        int wallCount = 0;
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                int wx = this.origin.x + x;
                int wy = this.origin.y + y;
                if (isWallCell(x, y)) {
                    //墙格：不可走，且记录用于膨胀
                    this.wallCellsX[wallCount] = x;
                    this.wallCellsY[wallCount] = y;
                    wallCount++;
                } else {
                    this.walkable[y][x] = isWalkableBase(wx, wy);
                }
            }
        }

        //障碍膨胀：墙格周围"实体半径"范围内的格子都标记为不可走
        //实体中心在格 g、半径为 r 时，碰撞箱与墙格相交当且仅当格距 < r + 0.5（相切不算）
        int inflate = (int) Math.ceil(entityRadius + 0.5f) - 1;
        if (inflate > 0) {
            for (int w = 0; w < wallCount; w++) {
                int wx = this.wallCellsX[w];
                int wy = this.wallCellsY[w];
                for (int dy = -inflate; dy <= inflate; dy++) {
                    int ny = wy + dy;
                    if (ny < 0 || ny >= SIZE) continue;
                    for (int dx = -inflate; dx <= inflate; dx++) {
                        int nx = wx + dx;
                        if (nx < 0 || nx >= SIZE) continue;
                        this.walkable[ny][nx] = false;
                    }
                }
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

                //该格不可走（墙体/水/膨胀区域等）
                if (!this.walkable[ny][nx]) continue;
                //对角线移动需要检查两个相邻格，防止穿角
                if (DIR_OFFSETS[i][0] != 0 && DIR_OFFSETS[i][1] != 0) {
                    if (!this.walkable[y][nx] || !this.walkable[ny][x]) {
                        continue;
                    }
                }

                this.cost[ny][nx] = (byte) (curCost + 1);
                //记录从该格指向当前格的方向（即走向代价更小的邻居，走向目标）
                //注意：i 是"从当前格 (x,y) 到邻居 (nx,ny)"的方向，反方向 (i+4)%8 才是"从邻居走向当前格"
                this.dirIndex[ny][nx] = (byte) ((i + 4) % 8);
                this.queueX[tail] = nx;
                this.queueY[tail] = ny;
                tail++;
            }
        }
    }

    /**
     * 判断流场内某局部坐标对应的格子是否为墙（用于障碍膨胀）
     */
    private boolean isWallCell (int lx, int ly) {
        int chunkX = lx / Chunk.ChunkWidth;
        int chunkY = ly / Chunk.ChunkHeight;
        Chunk chunk = this.chunkCache[chunkY][chunkX];
        if (chunk == null) return false;
        int wx = this.origin.x + lx;
        int wy = this.origin.y + ly;
        GridPoint2 local = Chunk.worldToChunk(wx, wy);
        return chunk.getWall(local.x, local.y) != null;
    }

    /**
     * 判断某个世界坐标对应的格子基础可走性（不膨胀）
     * <p>
     * 使用构建时缓存的区块引用直接访问数组，避免 getBlock/getWall 的线性遍历区块列表开销
     */
    private boolean isWalkableBase (int wx, int wy) {
        int chunkX = (int) Math.floor(wx / (float) Chunk.ChunkWidth) - (int) Math.floor(this.origin.x / (float) Chunk.ChunkWidth);
        int chunkY = (int) Math.floor(wy / (float) Chunk.ChunkHeight) - (int) Math.floor(this.origin.y / (float) Chunk.ChunkHeight);
        if (chunkX < 0 || chunkX >= CHUNK_COUNT || chunkY < 0 || chunkY >= CHUNK_COUNT) return false;
        Chunk chunk = this.chunkCache[chunkY][chunkX];
        //区块未加载：不可走（保守，避免规划进未知区域后卡死）
        if (chunk == null) return false;
        GridPoint2 local = Chunk.worldToChunk(wx, wy);
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
     * 查询某世界坐标的远离方向（指向代价最大的可达邻居，即远离流场目标）
     * <p>
     * 用于"与目标保持距离"和"受击逃跑"等本能行为；
     * 与 {@link #getDirection} 一样 O(1)，且会沿着流场平滑绕墙
     * @return 有可达邻居返回方向；不可达（被墙围死/场外/未构建）返回 null
     */
    public Direction getAwayDirection (float worldX, float worldY) {
        int localX = (int) Math.floor(worldX) - this.origin.x;
        int localY = (int) Math.floor(worldY) - this.origin.y;
        if (localX < 0 || localX >= SIZE || localY < 0 || localY >= SIZE) return null;
        //自身格不可达（被墙围死）时无法远离
        if (this.dirIndex[localY][localX] < 0) return null;

        int bestDir = -1;
        int bestCost = Integer.MIN_VALUE;
        for (int i = 0; i < DIR_OFFSETS.length; i++) {
            int nx = localX + DIR_OFFSETS[i][0];
            int ny = localY + DIR_OFFSETS[i][1];
            if (nx < 0 || nx >= SIZE || ny < 0 || ny >= SIZE) continue;
            int neighborCost = this.cost[ny][nx];
            //只考虑可达的邻居
            if (neighborCost < 0) continue;
            if (neighborCost > bestCost) {
                bestCost = neighborCost;
                bestDir = i;
            }
        }
        if (bestDir < 0) return null;
        return DIRECTIONS[bestDir];
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

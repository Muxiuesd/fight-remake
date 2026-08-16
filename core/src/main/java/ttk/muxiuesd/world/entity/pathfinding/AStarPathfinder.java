package ttk.muxiuesd.world.entity.pathfinding;

import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.BinaryHeap;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.world.chunk.Chunk;

/**
 * A* 寻路器
 * <p>
 * 用于"实体追逐自己的独立目标"的场景（如走向指定方块、各自逃走）：
 * 每个实体计算一条属于自己的路径，路径可缓存复用。
 * <p>
 * 搜索区域为起点与目标的包围盒（限制最大尺寸），节点复用避免分配；
 * 可走性判定与流场一致（墙不可走、水按实体的游泳能力、未加载区块不可走、按实体碰撞箱膨胀）
 */
public class AStarPathfinder {
    /// 搜索区域的最大边长（格子数），超过则放弃寻路（回退直线）
    public static final int MAX_AREA = 64;
    /// 最大扩展节点数（防止极端情况卡帧）
    private static final int MAX_EXPANSIONS = 2048;
    /// 8 邻域偏移
    private static final int[][] DIRS = {
        {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}
    };
    private static final float SQRT2 = 1.41421356f;

    //节点复用网格（x/y 用数组下标表示局部坐标）
    private final Node[][] nodes = new Node[MAX_AREA][MAX_AREA];
    //open 集合（最小堆，按 f 值）
    private final BinaryHeap<Node> open = new BinaryHeap<>(256, false);
    //本次搜索的版本号（用于节点访问标记，避免每次清空数组）
    private int version = 0;
    //搜索区域对应的区块缓存（区域最大约 64×64，但未对齐区块边界时最多覆盖 6×6 区块）
    private final Chunk[][] chunkCache = new Chunk[6][6];
    private int cacheOriginChunkX, cacheOriginChunkY;   //缓存左上角的区块坐标

    public AStarPathfinder () {
        for (int y = 0; y < MAX_AREA; y++) {
            for (int x = 0; x < MAX_AREA; x++) {
                this.nodes[y][x] = new Node();
            }
        }
    }

    /**
     * 计算一条从起点到目标的路径
     * @param cs 区块系统
     * @param startX 起点世界坐标
     * @param startY 起点世界坐标
     * @param targetX 目标世界坐标
     * @param targetY 目标世界坐标
     * @param entityRadius 实体碰撞箱半径（世界单位），用于障碍膨胀
     * @param canSwim 实体是否可游泳（可游泳则水方块可走）
     * @return 路径（路径点为格子中心的世界坐标）；不可达/超范围/无路返回 null
     */
    public PathWay findPath (ChunkSystem cs,
                             float startX, float startY,
                             float targetX, float targetY,
                             float entityRadius, boolean canSwim) {
        //起点与目标的格子坐标
        int startGX = (int) Math.floor(startX);
        int startGY = (int) Math.floor(startY);
        int targetGX = (int) Math.floor(targetX);
        int targetGY = (int) Math.floor(targetY);

        //搜索区域：起点与目标的包围盒 + 膨胀边距
        int inflate = (int) Math.ceil(entityRadius + 0.5f) - 1;
        int minX = Math.min(startGX, targetGX) - inflate - 2;
        int minY = Math.min(startGY, targetGY) - inflate - 2;
        int sizeX = Math.abs(startGX - targetGX) + inflate * 2 + 5;
        int sizeY = Math.abs(startGY - targetGY) + inflate * 2 + 5;
        if (sizeX > MAX_AREA || sizeY > MAX_AREA) return null;

        //缓存搜索区域覆盖的区块引用（区域最大 64×64，未对齐区块边界时最多覆盖 6×6 区块）
        this.cacheOriginChunkX = (int) Math.floor(minX / (float) Chunk.ChunkWidth);
        this.cacheOriginChunkY = (int) Math.floor(minY / (float) Chunk.ChunkHeight);
        int maxChunkX = (int) Math.floor((minX + sizeX - 1) / (float) Chunk.ChunkWidth) - this.cacheOriginChunkX;
        int maxChunkY = (int) Math.floor((minY + sizeY - 1) / (float) Chunk.ChunkHeight) - this.cacheOriginChunkY;
        for (int cy = 0; cy <= maxChunkY; cy++) {
            for (int cx = 0; cx <= maxChunkX; cx++) {
                this.chunkCache[cy][cx] = cs.getChunk(this.cacheOriginChunkX + cx, this.cacheOriginChunkY + cy);
            }
        }

        //起点和目标的局部坐标
        int startLX = startGX - minX;
        int startLY = startGY - minY;
        int targetLX = targetGX - minX;
        int targetLY = targetGY - minY;

        //版本号递增，标记本次搜索
        this.version++;
        this.open.clear();

        //起点入 open
        Node startNode = this.nodes[startLY][startLX];
        startNode.x = startLX;
        startNode.y = startLY;
        startNode.g = 0;
        startNode.f = heuristic(startLX, startLY, targetLX, targetLY);
        startNode.parentDir = -1;
        startNode.version = this.version;
        startNode.inOpen = true;
        startNode.processed = false;
        this.open.add(startNode, startNode.f);

        boolean found = false;
        int expansions = 0;
        while (!this.open.isEmpty() && expansions < MAX_EXPANSIONS) {
            Node cur = this.open.pop();
            //旧版本或已处理过的重复条目跳过
            if (cur.version != this.version || cur.processed) continue;
            cur.inOpen = false;
            cur.processed = true;
            expansions++;

            //到达目标（节点在网格中的位置即其坐标，用引用比较）
            if (cur == this.nodes[targetLY][targetLX]) {
                found = true;
                break;
            }

            //扩展 8 邻域
            int curX = cur.x;
            int curY = cur.y;
            for (int i = 0; i < DIRS.length; i++) {
                int nx = curX + DIRS[i][0];
                int ny = curY + DIRS[i][1];
                if (nx < 0 || nx >= sizeX || ny < 0 || ny >= sizeY) continue;

                //该格不可走（墙/水按游泳/膨胀等）
                if (!this.isWalkable(cs, nx + minX, ny + minY, entityRadius, canSwim, minX, minY, sizeX, sizeY)) continue;
                //对角线穿角检查
                if (DIRS[i][0] != 0 && DIRS[i][1] != 0) {
                    if (!this.isWalkable(cs, curX + DIRS[i][0] + minX, curY + minY, entityRadius, canSwim, minX, minY, sizeX, sizeY)
                        || !this.isWalkable(cs, curX + minX, curY + DIRS[i][1] + minY, entityRadius, canSwim, minX, minY, sizeX, sizeY)) {
                        continue;
                    }
                }

                Node neighbor = this.nodes[ny][nx];
                neighbor.x = nx;
                neighbor.y = ny;
                //旧版本节点：重置
                if (neighbor.version != this.version) {
                    neighbor.version = this.version;
                    neighbor.inOpen = false;
                    neighbor.processed = false;
                    neighbor.g = Float.POSITIVE_INFINITY;
                }
                //已处理过（closed）的节点跳过
                if (neighbor.processed) continue;

                float moveCost = (DIRS[i][0] != 0 && DIRS[i][1] != 0) ? SQRT2 : 1f;
                float newG = cur.g + moveCost;
                if (newG < neighbor.g) {
                    neighbor.g = newG;
                    neighbor.f = newG + heuristic(nx, ny, targetLX, targetLY);
                    neighbor.parentDir = i;
                    if (!neighbor.inOpen) {
                        //首次加入 open
                        neighbor.inOpen = true;
                        this.open.add(neighbor, neighbor.f);
                    } else {
                        //已在 open 中：更新堆中的值（libGDX BinaryHeap 的 setValue 会重新调整堆位置）
                        this.open.setValue(neighbor, neighbor.f);
                    }
                }
            }
        }

        if (!found) return null;

        //回溯路径：从目标沿 parentDir 反推，路径点不含起点
        int pathLen = 0;
        {
            int x = targetLX, y = targetLY;
            Node node = this.nodes[y][x];
            while (node.parentDir != -1) {
                pathLen++;
                x -= DIRS[node.parentDir][0];
                y -= DIRS[node.parentDir][1];
                node = this.nodes[y][x];
            }
        }

        //收集路径点（世界坐标 = 格子坐标 + 0.5 中心点）
        float[] wayX = new float[pathLen];
        float[] wayY = new float[pathLen];
        int index = pathLen - 1;
        {
            int x = targetLX, y = targetLY;
            Node node = this.nodes[y][x];
            while (node.parentDir != -1) {
                wayX[index] = x + minX + 0.5f;
                wayY[index] = y + minY + 0.5f;
                index--;
                x -= DIRS[node.parentDir][0];
                y -= DIRS[node.parentDir][1];
                node = this.nodes[y][x];
            }
        }

        return new PathWay(wayX, wayY);
    }

    /**
     * 计算启发式（八方向移动的切比雪夫距离近似）
     */
    private float heuristic (int x, int y, int targetX, int targetY) {
        int dx = Math.abs(x - targetX);
        int dy = Math.abs(y - targetY);
        return Math.max(dx, dy) + (SQRT2 - 1f) * Math.min(dx, dy);
    }

    /**
     * 判断实体中心位于某格时是否可行走
     * <p>
     * 与流场的判定一致：墙不可走、不可走方块（按游泳能力放行水）、未加载区块不可走、按实体碰撞箱膨胀
     * @param wx 世界格子坐标
     * @param wy 世界格子坐标
     */
    private boolean isWalkable (ChunkSystem cs, int wx, int wy,
                                float entityRadius, boolean canSwim,
                                int areaMinX, int areaMinY, int sizeX, int sizeY) {
        int localX = wx - areaMinX;
        int localY = wy - areaMinY;
        if (localX < 0 || localX >= sizeX || localY < 0 || localY >= sizeY) return false;

        Chunk chunk = this.getCachedChunk(wx, wy);
        //区块未加载：不可走（保守）
        if (chunk == null) return false;

        GridPoint2 local = Chunk.worldToChunk(wx, wy);
        //墙：不可走
        if (chunk.getWall(local.x, local.y) != null) return false;

        var block = chunk.getBlock(local.x, local.y);
        //方块为 null（旧存档缺格）：不可走
        if (block == null) return false;

        boolean baseWalkable = block.getProperty().isWalkable();
        //不可走方块中，水（swimmable）在实体可游泳时放行
        if (!baseWalkable && canSwim && block.getProperty().isSwimmable()) {
            baseWalkable = true;
        }
        if (!baseWalkable) return false;

        //障碍膨胀：实体中心距离墙 >= 实体半径
        int inflate = (int) Math.ceil(entityRadius + 0.5f) - 1;
        if (inflate > 0) {
            for (int dy = -inflate; dy <= inflate; dy++) {
                for (int dx = -inflate; dx <= inflate; dx++) {
                    int checkX = wx + dx;
                    int checkY = wy + dy;
                    int checkLocalX = checkX - areaMinX;
                    int checkLocalY = checkY - areaMinY;
                    if (checkLocalX < 0 || checkLocalX >= sizeX || checkLocalY < 0 || checkLocalY >= sizeY) continue;
                    Chunk checkChunk = this.getCachedChunk(checkX, checkY);
                    if (checkChunk == null) continue;
                    GridPoint2 checkLocal = Chunk.worldToChunk(checkX, checkY);
                    if (checkChunk.getWall(checkLocal.x, checkLocal.y) != null) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * 从本次搜索的区块缓存中获取某世界坐标对应的区块
     */
    private Chunk getCachedChunk (int wx, int wy) {
        int chunkX = (int) Math.floor(wx / (float) Chunk.ChunkWidth) - this.cacheOriginChunkX;
        int chunkY = (int) Math.floor(wy / (float) Chunk.ChunkHeight) - this.cacheOriginChunkY;
        if (chunkX < 0 || chunkX >= 6 || chunkY < 0 || chunkY >= 6) return null;
        return this.chunkCache[chunkY][chunkX];
    }

    /**
     * A* 节点（复用，x/y 为局部坐标）
     */
    private static class Node extends BinaryHeap.Node implements Comparable<Node> {
        int x, y;               //局部坐标（数组下标）
        float g;                //起点到该节点的代价
        float f;                //g + 启发式
        int parentDir = -1;     //从父节点到该节点的方向索引，-1 表示起点
        int version = -1;       //所属搜索版本
        boolean inOpen;         //是否在 open 中
        boolean processed;      //是否已处理过（closed）

        Node () {
            super(0);
        }

        @Override
        public int compareTo (Node o) {
            return Float.compare(this.f, o.f);
        }
    }
}

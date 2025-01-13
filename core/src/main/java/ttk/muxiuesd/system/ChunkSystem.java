package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.util.*;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.Block;
import ttk.muxiuesd.world.chunk.Chunk;
import ttk.muxiuesd.world.chunk.ChunkLoadTask;
import ttk.muxiuesd.world.chunk.ChunkUnloadTask;
import ttk.muxiuesd.world.entity.Player;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.*;

public class ChunkSystem extends GameSystem{
    public final String TAG = this.getClass().getName();

    public boolean chunkEdgeRender = false;
    public boolean wallHitboxRender = false;

    private Player player;
    private Vector2 playerLastPosition;
    private WorldMapNoise noise;

    public static final float Slope = 100.0f;   // 地形坡度，生成地形时的参数
    private float visualRange = 64f;

    // 线程池
    private ExecutorService executor;

    // 当前活跃的线程
    private ArrayList<Chunk> activeChunks;
    // 加载和卸载区块的延迟队列
    private ArrayList<Chunk> _loadChunks = new ArrayList<>();
    private ArrayList<Chunk> _unloadChunks = new ArrayList<>();

    private ConcurrentHashMap<ChunkPosition, Future<Chunk>> chunkLoadingTasks;
    private ConcurrentHashMap<ChunkPosition, Future<Chunk>> chunkUnloadingTasks;


    public ChunkSystem(World world) {
        super(world);

        this.noise = new WorldMapNoise((int) (Math.random() * 10000));
        EntitySystem es = (EntitySystem) getWorld().getWorldSystem().getSystem("EntitySystem");
        PlayerSystem ps = (PlayerSystem) getWorld().getWorldSystem().getSystem("PlayerSystem");
        this.player = ps.getPlayer();

        this.playerLastPosition = new Vector2(this.player.x + 10000, this.player.y + 10000);

        this.initPool();

        // 初始化活跃区块队列
        this.activeChunks = new ArrayList<>();

        // 预加载一次
        this.update(0);
        Log.print(TAG, "ChunkSystem初始化完成！");
    }

    private float loadChunkTick = 0.2f;
    private float span = 0.2f;

    @Override
    public void update(float delta) {
        if (delta == 0) {
            // 预加载
            // 先强制加载一次玩家所在的区块
            ChunkPosition playerChunkPosition = this.getPlayerChunkPosition();
            Chunk chunk = this.initChunk(playerChunkPosition.getX(), playerChunkPosition.getY());
            this.activeChunks.add(chunk);
        }

        // 检查线程池里的区块是否加载完成
        if (!this.chunkLoadingTasks.isEmpty()) {
            for (ChunkPosition position : this.chunkLoadingTasks.keySet()) {
                if (this.isChunkLoaded(position)) {
                    // 如果加载完成，则将区块加入活跃队列
                    Chunk chunk = this.getLoadedChunk(position);
                    if (chunk != null) {
                        this._loadChunks.add(chunk);
                    }
                    this.chunkLoadingTasks.remove(position);
                }
            }
        }
        if (!this.chunkUnloadingTasks.isEmpty()) {
            for (ChunkPosition position : this.chunkUnloadingTasks.keySet()) {
                if (this.isChunkUnloaded(position)) {
                    Chunk chunk = this.getUnloadedChunk(position);
                    if (chunk != null) {
                        this._unloadChunks.remove(chunk);
                    }
                    this.chunkUnloadingTasks.remove(position);
                }
            }
        }

        if (!this._loadChunks.isEmpty()) {
            this.activeChunks.addAll(this._loadChunks);
            this._loadChunks.clear();
        }
        if (!this._unloadChunks.isEmpty()) {
            // this._unloadChunks.forEach(Chunk::dispose);
            for (Chunk chunk : this._unloadChunks) {
                // 生成任务并提交到线程池里卸载区块
                ChunkUnloadTask task = new ChunkUnloadTask(this, chunk);
                Future<Chunk> future = this.executor.submit(task);
                this.chunkUnloadingTasks.put(chunk.getChunkPosition(), future);
            }
            this.activeChunks.removeAll(this._unloadChunks);
            // System.out.println(this.activeChunks.size());
            // this._unloadChunks.clear();
        }

        // 更新正在活跃的区块
        for (Chunk chunk : this.activeChunks) {
            chunk.update(delta);
        }

        if (span >= loadChunkTick && this.playerMoved()) {
            this.calculateNeedLoadedChunk();
            this.calculateNeedUnloadedChunk();
            span = 0;
            this.playerLastPosition.set(this.player.x, this.player.y);
            // Log.print(TAG, "计算需要加载和卸载的区块");
        } else if (span <= loadChunkTick) {
            // 防止玩家停止太久导致span超出最大值
            span += delta;
        }
    }

    @Override
    public void draw(Batch batch) {
        batch.setColor(Color.WHITE);
        for (Chunk chunk : this.activeChunks) {
            chunk.draw(batch);
        }
    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        for (Chunk chunk : this.activeChunks) {
            chunk.renderShape(batch);
        }
    }

    @Override
    public void dispose() {
        for (Chunk activeChunk : this.activeChunks) {
            activeChunk.dispose();
        }
        this.shutdownChunkLoadPool();
    }

    /**
     * 计算需要被加载的区块
     */
    private void calculateNeedLoadedChunk() {
        // 计算玩家所在区块坐标编号
        ChunkPosition chunkPosition = this.getPlayerChunkPosition(this.player);
        int playerChunkX = chunkPosition.getX();
        int playerChunkY = chunkPosition.getY();

        // System.out.println("("+ player.x+ "," + player.y +")" + "("+ playerChunkX + "," + playerChunkY +")");
        // TODO 实现更好的循环
        for (int y = -2; y < 3; y++) {
            for (int x = -2; x < 3; x++) {
                int newChunkX = playerChunkX + x;
                int newChunkY = playerChunkY + y;

                float distance = Util.getDistance(
                    this.player.x + this.player.width / 2,
                    this.player.y + this.player.height / 2,
                    newChunkX * Chunk.ChunkWidth + Chunk.ChunkWidth / 2f,
                    newChunkY * Chunk.ChunkHeight + Chunk.ChunkHeight / 2f);

                if (distance <= this.visualRange) {
                    this.loadChunk(newChunkX, newChunkY);
                }
            }
        }
    }

    /**
     * 计算需要被卸载的区块
     */
    private void calculateNeedUnloadedChunk() {
        for (Chunk chunk : this.activeChunks) {
            float distance = Util.getDistance(this.player.x, this.player.y,
                chunk.getChunkPosition().getX() * Chunk.ChunkWidth + Chunk.ChunkWidth / 2f,
                chunk.getChunkPosition().getY() * Chunk.ChunkHeight + Chunk.ChunkHeight / 2f);
            if (distance > this.visualRange) {
                this.unloadChunk(chunk);
            }
        }
    }

    /**
     * 加载区块
     */
    public void loadChunk(int chunkX, int chunkY) {
        boolean chunkExist = false;
        ChunkPosition loadChunkPosition = new ChunkPosition(chunkX, chunkY);

        // 检查是否已经在加载队列里
        for (Chunk chunk : this._loadChunks) {
            ChunkPosition chunkPosition = chunk.getChunkPosition();
            if (chunkPosition.equals(loadChunkPosition)) {
                // Log.error(TAG, "编号为：(" + chunkX +","+ chunkY +")的区块已加载！！！");
                chunkExist = true;
                break;
            }
        }
        // 检查是否已经在活跃队列里
        for (Chunk chunk : this.activeChunks) {
            ChunkPosition chunkPosition = chunk.getChunkPosition();
            if (chunkPosition.equals(loadChunkPosition)) {
                // Log.error(TAG, "编号为：(" + chunkX +","+ chunkY +")的区块已活跃！！！");
                chunkExist = true;
                break;
            }
        }
        if (chunkExist) {
            // TODO 实现如果区块已存在的更多操作，比如加载保存的此区块的数据
            return;
        }

       /* Chunk chunk = this.initChunk(chunkX, chunkY);
        this._loadChunks.add(chunk);*/

        // 生成任务并提交到线程池里加载区块
        ChunkLoadTask task = new ChunkLoadTask(this, loadChunkPosition);
        Future<Chunk> future = this.executor.submit(task);
        this.chunkLoadingTasks.put(loadChunkPosition, future);
    }

    /**
     * 检查区块是否加载已经完成
     */
    public boolean isChunkLoaded(ChunkPosition position) {
        Future<Chunk> future = this.chunkLoadingTasks.get(position);
        return future != null && future.isDone();
    }

    /**
     * 获取已加载的区块
     */
    public Chunk getLoadedChunk(ChunkPosition position) {
        Future<Chunk> future = this.chunkLoadingTasks.get(position);
        if (future != null && future.isDone()) {
            try {
                return future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    /**
     * 卸载区块
     */
    public void unloadChunk(Chunk chunk) {
        boolean removed = false;
        // 先在正在加载的任务里面找
        Future<Chunk> future = this.chunkLoadingTasks.remove(chunk.getChunkPosition());
        if (future != null && !future.isDone()) {
            future.cancel(true);    // 取消未完成的任务
            return;
        }

        // 后在加载延迟队列里查找
        for (Chunk loadChunk : this._loadChunks) {
            if (Objects.equals(chunk, loadChunk)) {
                this._loadChunks.remove(chunk);
                removed = true;
                // Log.print(TAG, "卸载编号为：(" + chunk.chunkX +","+ chunk.chunkY +")的区块");
                break;
            }
        }
        if (removed) {
            return;
        }
        // 执行到这就是要卸载的区块在活跃队列里，延迟卸载
        this._unloadChunks.add(chunk);
        // Log.print(TAG, "卸载编号为：(" + chunk.chunkX +","+ chunk.chunkY +")的区块");
    }

    /**
     * 检查区块是否已经卸载完成
     */
    public boolean isChunkUnloaded(ChunkPosition position) {
        Future<Chunk> future = this.chunkUnloadingTasks.get(position);
        return future != null && future.isDone();
    }

    /**
     * 获取已卸载的区块
     */
    public Chunk getUnloadedChunk(ChunkPosition position) {
        Future<Chunk> future = this.chunkUnloadingTasks.get(position);
        if (future != null && future.isDone()) {
            try {
                return future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }



    /**
     * 初始化线程池
     */
    private void initPool() {
        int coreSize = Runtime.getRuntime().availableProcessors();
        Log.print(TAG, "初始化区块加载线程池，核心线程数：" + coreSize);
        this.executor = Executors.newFixedThreadPool(coreSize);
        this.chunkLoadingTasks = new ConcurrentHashMap<>();
        this.chunkUnloadingTasks = new ConcurrentHashMap<>();
    }

    /**
     * 初始化区块
     */
    private Chunk initChunk(int chunkX, int chunkY) {
        Chunk chunk = new Chunk(this);
        chunk.setChunkPosition(new ChunkPosition(chunkX, chunkY));
        chunk.initBlock();
        chunk.initWall();
        return chunk;
    }

    /**
     * 获取世界坐标上对应的方块
     *
     * @return
     */
    public Block getBlock(float wx, float wy) {
        int nx = (int) Util.fastRound(wx);
        int ny = (int) Util.fastRound(wy);

        BlockPosition blockPosition = new BlockPosition(nx, ny);
        ChunkPosition chunkPosition = this.getChunkPosition(wx, wy);

        Chunk chunk = this.getChunk(chunkPosition);
        if (chunk == null) {
            Log.error(TAG, "获取的区块为null！！！");
            throw new RuntimeException(chunkPosition.toString() + "这个区块坐标对应的区块为null，可能是还未加载！！！");
        }
        Block block = chunk.seekBlock(blockPosition.getX(), blockPosition.getY());
        // 如果在当前区块找不到的话
        if (block == null) {
            // 在临近区块寻找
            for (int chunkY = -1; chunkY < 2; chunkY++) {
                for (int chunkX = -1; chunkX < 2; chunkX++) {
                    int cx = chunkPosition.getX() + chunkX;
                    int cy = chunkPosition.getY() + chunkY;
                    ChunkPosition chunkPosition1 = new ChunkPosition(cx, cy);
                    // 跳过已寻找过的区块
                    if (chunkPosition1.equals(chunkPosition)) {
                        continue;
                    }

                    Chunk chunk1 = this.getChunk(chunkPosition1);
                    // 有可能是还未加载的区块
                    if (chunk1 == null) {
                        // TODO 解决在还未加载的区块中查找方块
                        Log.print(TAG, chunkPosition1.toString() + "区块尚未加载！！！");
                        continue;
                    }
                    Block block1 = chunk1.seekBlock(blockPosition.getX(), blockPosition.getY());
                    if (block1 != null) {
                        return block1;
                    }
                }
            }

            Log.print(TAG, "尽力了");
        }
        // 运行到这里应该就是查找到方块了
        return block;
    }

    public Chunk getChunk(int chunkX, int chunkY) {
        return this.getChunk(new ChunkPosition(chunkX, chunkY));
    }

    /**
     * 获取区块
     */
    public Chunk getChunk(ChunkPosition chunkPosition) {
        for (Chunk chunk : this.activeChunks) {
            if (chunk.getChunkPosition().equals(chunkPosition)) {
                // 如果正在活跃的区块里有这个区块则返回对应的区块
                return chunk;
            }
        }
        // 如果没有这个区块,暂时就这么处理
        // TODO
        return null;
    }

    /**
     * 核心算法：
     * 获取玩家所在的区块编号
     */
    private ChunkPosition getPlayerChunkPosition(Player player) {
        Vector2 playerCenter = player.getPlayerCenter();
        return this.getChunkPosition(playerCenter.x, playerCenter.y);
    }

    /**
     * 获取世界坐标所对应的区块编号
     *
     * @param wx
     * @param wy
     * @return
     */
    private ChunkPosition getChunkPosition(float wx, float wy) {
        ChunkPosition cp = new ChunkPosition();

        cp.setX((int) (Math.abs(Util.fastRound(wx)) / Chunk.ChunkWidth));
        cp.setY((int) (Math.abs(Util.fastRound(wy)) / Chunk.ChunkHeight));

        if (wx < 0) {
            cp.setX(-1 * cp.getX() - 1);
        }
        if (wy < 0) {
            cp.setY(-1 * cp.getY() - 1);
        }
        return cp;
    }

    /**
     * 对外开放的方法：获取玩家目前所在区块的编号（区块坐标）
     *
     * @return
     */
    public ChunkPosition getPlayerChunkPosition() {
        return this.getPlayerChunkPosition(this.player);
    }

    // 移动阈值
    private float moveValue = 1.2f;

    /**
     * 如果玩家一段时间内移动距离大于这个阈值就返回true，否则为false
     */
    private boolean playerMoved() {
        com.badlogic.gdx.math.Vector2 lastVector2 = this.playerLastPosition;
        float distance = Util.getDistance(this.player, lastVector2.x, lastVector2.y);
        return distance >= moveValue;
    }

    /**
     * 关闭线程池
     */
    public void shutdownChunkLoadPool() {
        this.executor.shutdown();
        try {
            if (!this.executor.awaitTermination(5, TimeUnit.SECONDS)) {
                this.executor.shutdownNow();
                Log.print(TAG, "区块任务线程池关闭");
            }
        } catch (InterruptedException e) {
            this.executor.shutdownNow();
            throw new RuntimeException(e);
        }
    }
    public WorldMapNoise getNoise() {
        return this.noise;
    }

}

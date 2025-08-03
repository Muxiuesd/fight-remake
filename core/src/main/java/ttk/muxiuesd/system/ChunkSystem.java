package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterBlockReplace;
import ttk.muxiuesd.interfaces.render.IWorldChunkRender;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.registry.WorldInformationType;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.*;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.chunk.Chunk;
import ttk.muxiuesd.world.chunk.ChunkLoadTask;
import ttk.muxiuesd.world.chunk.ChunkUnloadTask;
import ttk.muxiuesd.world.chunk.MainWorldChunkGenerator;
import ttk.muxiuesd.world.chunk.abs.ChunkGenerator;
import ttk.muxiuesd.world.entity.Player;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * 区块系统
 * */
public class ChunkSystem extends WorldSystem implements IWorldChunkRender {
    public final String TAG = this.getClass().getName();

    public boolean chunkEdgeRender = false;
    public boolean wallHitboxRender = false;
    public static final float Slope = 100.0f;   // 地形坡度，生成地形时的参数

    private Player player;
    private Vector2 playerLastPosition;
    private WorldMapNoise noise;
    private Timer chunkLoadTimer = new Timer(0.5f, 0.5f);

    //方块实例，不带有方块实体的同一种方块在world里只有一个实例，带有方块实体的方块都是单独一个实例
    private final ConcurrentHashMap<String, Block> blockInstances = new ConcurrentHashMap<>();
    //方块实体，每一个方块实体都是一个单独的实例
    private final ConcurrentHashMap<BlockWithEntity<?, ?>, BlockEntity> blockEntities = new ConcurrentHashMap<>();

    // 当前活跃的线程
    private ArrayList<Chunk> activeChunks = new ArrayList<>();
    // 加载和卸载区块的延迟队列
    private ArrayList<Chunk> _loadChunks = new ArrayList<>();
    private ArrayList<Chunk> _unloadChunks = new ArrayList<>();
    // 线程池
    private ExecutorService executor;
    private ConcurrentHashMap<ChunkPosition, Future<Chunk>> chunkLoadingTasks;
    private ConcurrentHashMap<ChunkPosition, Future<Chunk>> chunkUnloadingTasks;


    public ChunkSystem(World world) {
        super(world);
        WorldInformationType.INT.putIfAbsent("seed", 114514);
    }

    @Override
    public void initialize () {
        //this.noise = new WorldMapNoise((int) (Math.random() * 10000));
        this.noise = new WorldMapNoise(WorldInformationType.INT.get("seed"));

        PlayerSystem ps = getWorld().getSystem(PlayerSystem.class);
        this.player = ps.getPlayer();
        this.playerLastPosition = new Vector2(this.player.x + 10000, this.player.y + 10000);

        this.initPool();

        // 预加载一次
        this.update(-1.2f);
        Log.print(TAG, "ChunkSystem初始化完成！");
    }

    @Override
    public void update(float delta) {
        if (delta == -1.2f) {
            // 预加载
            // 先强制加载一次玩家所在的区块
            ChunkPosition playerChunkPosition = this.getPlayerChunkPosition();
            Chunk chunk = this.initChunk(playerChunkPosition.getX(), playerChunkPosition.getY());
            this.activeChunks.add(chunk);

            EntitySystem es = getWorld().getSystem(EntitySystem.class);
            es.initLoadEntities(playerChunkPosition);
            return;
        }

        PlayerSystem ps = getWorld().getSystem(PlayerSystem.class);
        this.player = ps.getPlayer();
        this.chunkLoadTimer.update(delta);

        // 检查线程池里的区块是否加载完成
        if (!this.chunkLoadingTasks.isEmpty()) {
            for (ChunkPosition position : this.chunkLoadingTasks.keySet()) {
                if (this.isChunkLoaded(position)) {
                    // 如果加载完成，则将区块加入活跃队列
                    Chunk chunk = this.getLoadedChunk(position);
                    if (chunk != null) {
                        //因为有量子幽灵线程，不这么写会导致初始加载的几个区块中的某个位置的区块凭空添加两个实例
                        boolean exit = false;
                        for (Chunk loadChunk : this._loadChunks) {
                            if (loadChunk.getChunkPosition().equals(chunk.getChunkPosition())) {
                                exit = true;
                                break;
                            }
                        }
                        if (!exit) this._loadChunks.add(chunk);
                        EntitySystem entitySystem = getManager().getSystem(EntitySystem.class);
                        entitySystem.loadEntities(this, chunk);
                    }
                    //移除任务
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
        }

        // 更新正在活跃的区块
        //Log.print(TAG, "____________");
        for (Chunk chunk : this.activeChunks) {
            chunk.update(delta);
            //Log.print(TAG, chunk.getChunkPosition().toString());
        }
        //Log.print(TAG, "____________");

        //更新方块实体
        for (BlockEntity blockEntity : this.blockEntities.values()) {
            blockEntity.update(delta);
        }


        if (this.chunkLoadTimer.isReady() && this.playerMoved()) {
            this.calculateNeedLoadedChunk();
            this.calculateNeedUnloadedChunk();
            this.playerLastPosition.set(this.player.x, this.player.y);
            // Log.print(TAG, "计算需要加载和卸载的区块");
        }
    }

    @Override
    public void draw(Batch batch) {
        for (Chunk chunk : this.activeChunks) {
            chunk.draw(batch);
        }
        //绘制方块实体
        this.getBlockEntities().forEach((block, blockEntity) -> {
            BlockPos pos = blockEntity.getBlockPos();
            blockEntity.draw(batch, pos.x, pos.y);
        });

    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        for (Chunk chunk : this.activeChunks) {
            chunk.renderShape(batch);
        }
    }

    @Override
    public void render (Batch batch, ShapeRenderer shapeRenderer) {
        this.draw(batch);
        this.renderShape(shapeRenderer);
    }

    @Override
    public int getRenderPriority () {
        return 100;
    }

    @Override
    public void dispose() {
        this.saveAllChunks();
        this.shutdownChunkLoadPool();
    }

    /**
     * 卸载保存现有的所有区块
     * */
    private void saveAllChunks() {
        Log.print(TAG, "保存游戏所有的区块信息。。。");
        for (Chunk chunk : this.activeChunks) {
            ChunkUnloadTask chunkUnloadTask = new ChunkUnloadTask(this, chunk);
            chunkUnloadTask.call();
        }
        Log.print(TAG, "游戏所有的区块信息完成保存！");
    }

    /**
     * 添加方块，确切地说，是把这个方块的实例放进区块系统里确保可以被查询到
     * */
    public void addBlock (Block block, float wx, float wy) {
        Vector2 floor = Util.fastFloor(wx, wy);
        //如果新的方块是带有方块实体的方块
        if (block instanceof BlockWithEntity<?, ?> blockWithEntity) {
            //添加方块实体
            BlockEntity blockEntity = blockWithEntity.createBlockEntity(new BlockPos(floor), getWorld());
            this.addBlockEntity(blockWithEntity, blockEntity);
            this.addBlockInstance(blockWithEntity);

            //TODO 事件：添加方块实体
        }else {
            this.addBlockInstance(block);
        }
    }

    /**
     * 移除方块
     * */
    public Block removeBlock (float wx, float wy) {
        Block removed = this.getBlock(wx, wy);
        Vector2 floor = Util.fastFloor(wx, wy);

        Chunk chunk = this.getChunk(this.getChunkPosition(floor.x, floor.y));
        GridPoint2 chunkBlockPos = chunk.worldToChunk(floor.x, floor.y);
        chunk.setBlock(null, chunkBlockPos.x, chunkBlockPos.y);

        return removed;
    }

    /**
     * 添加方块实例
     * */
    private void addBlockInstance (Block block) {
        //如果是带有方块实体的方块
        if (block instanceof BlockWithEntity<? ,?> blockWithEntity) {
            this.blockInstances.put(this.getBlockKey(blockWithEntity), blockWithEntity);
            return;
        }

        //普通方块
        if (! this.blockInstances.containsKey(block.getID())) {
            this.blockInstances.put(block.getID(), block);
        }
    }

    /**
     * 移除方块实例
     * */
    private Block removeBlockInstance (Block block) {
        //如果是带有方块实体的方块
        if (block instanceof BlockWithEntity<? ,?> blockWithEntity) {
            return this.blockInstances.remove(this.getBlockKey(blockWithEntity));
        }

        //普通方块
        if (!this.blockInstances.containsKey(block.getID())) {
            throw new IllegalArgumentException("方块：" + block.getID() + " 的实例从未添加过！！！");
        }

        return this.blockInstances.remove(block.getID());
    }

    /**
     * 添加方块实体
     * */
    private void addBlockEntity(BlockWithEntity<? ,?> block, BlockEntity blockEntity) {
        if (block == null || blockEntity == null) return;

        TimeSystem ts = getWorld().getSystem(TimeSystem.class);
        ts.add(blockEntity);
        this.getBlockEntities().put(block, blockEntity);

        blockEntity.bePlaced(getWorld(), this.player);
    }

    /**
     * 移除方块实体
     * */
    private BlockEntity removeBlockEntity (BlockWithEntity<?, ?> block) {
        BlockEntity removed = this.getBlockEntities().remove(block);
        if (removed == null) return null;

        TimeSystem ts = getWorld().getSystem(TimeSystem.class);
        ts.remove(removed);
        removed.beDestroyed(getWorld(), this.player);

        return removed;
    }

    /**
     * 替换某个位置的方块
     * @return 被替换下来的方块
     * */
    public Block replaceBlock(Block newBlock, float wx, float wy) {
        if (newBlock == null) {
            throw new NullPointerException("newBlock 不能为null！！！");
        }
        Block oldBlock = this.getBlock(wx, wy);
        Vector2 floor = Util.fastFloor(wx, wy);

        ChunkPosition chunkPosition = this.getChunkPosition(floor.x, floor.y);
        Chunk chunk = this.getChunk(chunkPosition);
        GridPoint2 chunkBlockPos = chunk.worldToChunk(floor.x, floor.y);

        //如果旧的方块是带有方块实体的方块
        if (oldBlock instanceof BlockWithEntity<? ,?> blockWithEntity) {
            this.removeBlockEntity(blockWithEntity);
            //TODO 事件：移除方块实体
            //移除对应的方块实例
            this.removeBlockInstance(oldBlock);
        }

        //如果新方块是带有方块实体的方块
        if (newBlock instanceof BlockWithEntity<? ,?> blockWithEntity) {
            //需要新建一个实例再添加
            BlockWithEntity<? ,?> self = blockWithEntity.createSelf();
            this.addBlock(self, wx, wy);
            chunk.setBlock(self, chunkBlockPos.x, chunkBlockPos.y);
        }else {
            this.addBlock(newBlock, wx, wy);
            chunk.setBlock(newBlock, chunkBlockPos.x, chunkBlockPos.y);
        }

        //chunk.setBlock(this.getBlockInstancesMap().get(this.getBlockKey(newBlock)), chunkBlockPos.x, chunkBlockPos.y);
         EventBus.post(EventTypes.BLOCK_REPLACE, new EventPosterBlockReplace(getWorld(), newBlock, oldBlock, wx, wy));

        return oldBlock;
    }


    /**
     * 计算需要被加载的区块
     */
    private void calculateNeedLoadedChunk() {
        // 计算玩家所在区块坐标编号
        ChunkPosition chunkPosition = this.getPlayerChunkPosition(this.player);
        int playerChunkX = chunkPosition.getX();
        int playerChunkY = chunkPosition.getY();
        Vector2 playerCenter = this.player.getCenter();

        // System.out.println("("+ player.x+ "," + player.y +")" + "("+ playerChunkX + "," + playerChunkY +")");
        // TODO 实现更好的循环
        Integer value = Fight.PLAYER_VISUAL_RANGE.getValue();
        for (int y = -value; y < value + 1; y++) {
            for (int x = -value; x < value + 1; x++) {
                int newChunkX = playerChunkX + x;
                int newChunkY = playerChunkY + y;
                float distance = Util.getDistance(
                    playerCenter.x, playerCenter.y,
                    newChunkX * Chunk.ChunkWidth + Chunk.ChunkWidth / 2f,
                    newChunkY * Chunk.ChunkHeight + Chunk.ChunkHeight / 2f
                );

                if (distance <= value * (Chunk.ChunkWidth + Chunk.ChunkHeight) / 2f) {
                    this.loadChunk(newChunkX, newChunkY);
                }
            }
        }
    }

    /**
     * 计算需要被卸载的区块
     */
    private void calculateNeedUnloadedChunk() {
        Integer value = Fight.PLAYER_VISUAL_RANGE.getValue();
        for (Chunk chunk : this.activeChunks) {
            float distance = Util.getDistance(this.player.x, this.player.y,
                chunk.getChunkPosition().getX() * Chunk.ChunkWidth + Chunk.ChunkWidth / 2f,
                chunk.getChunkPosition().getY() * Chunk.ChunkHeight + Chunk.ChunkHeight / 2f
            );

            if (distance > value * (Chunk.ChunkWidth + Chunk.ChunkHeight) / 2f) {
                this.unloadChunk(chunk);
            }
        }
    }

    /**
     * 加载区块
     */
    public void loadChunk(int chunkX, int chunkY) {
        ChunkPosition loadChunkPosition = new ChunkPosition(chunkX, chunkY);
        //检查是否加载过或者正在加载这个区块
        boolean chunkExist = this.chunkExist(loadChunkPosition);

        if (chunkExist) {
            // TODO 实现如果区块已存在的更多操作，比如加载保存的此区块的数据
            return;
        }

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

        //通知实体系统卸载区块上的实体
        EntitySystem es = getWorld().getSystem(EntitySystem.class);
        es.unloadEntities(this, chunk);

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
     * 根据区块坐标来判断区块是否已经存在
     * */
    private boolean chunkExist(ChunkPosition position) {
        //在活跃区块里找
        for (Chunk chunk : this.activeChunks) {
            if (chunk.getChunkPosition().equals(position)) return true;
        }

        //在加载任务里面找
        ConcurrentHashMap.KeySetView<ChunkPosition, Future<Chunk>> chunkPositions = this.chunkLoadingTasks.keySet();
        for (ChunkPosition chunkPosition : chunkPositions) {
            if (chunkPosition.equals(position)) return true;
        }

        //后在加载延迟队列里查找
        for (Chunk loadChunk : this._loadChunks) {
            if (loadChunk.getChunkPosition().equals(position)) return true;
        }

        return false;
    }

    /**
     * 初始化线程池
     */
    private void initPool() {
        int coreSize = Runtime.getRuntime().availableProcessors();
        Log.print(TAG, "初始化区块加载线程池，核心线程数：" + coreSize);
        this.executor = Executors.newFixedThreadPool(48);
        this.chunkLoadingTasks = new ConcurrentHashMap<>();
        this.chunkUnloadingTasks = new ConcurrentHashMap<>();
    }

    /**
     * （主线程）加载区块
     */
    public Chunk initChunk(int chunkX, int chunkY) {
        ChunkLoadTask chunkLoadTask = new ChunkLoadTask(this, new ChunkPosition(chunkX, chunkY));
        return chunkLoadTask.call();
    }

    /**
     * 获取方块
     * @param position 世界坐标
     */
    public Block getBlock (Vector2 position) {
        return this.getBlock(position.x, position.y);
    }
    /**
     * 获取世界坐标上对应的方块
     * @param wx 世界x坐标
     * @param wy 世界y坐标
     * @return
     */
    public Block getBlock(float wx, float wy) {
        Vector2 floor = Util.fastFloor(wx, wy);
        ChunkPosition chunkPosition = this.getChunkPosition(floor.x, floor.y);

        Chunk chunk = this.getChunk(chunkPosition);
        if (chunk == null) {
            /*Log.error(TAG, "获取的区块为null！！！");
            throw new RuntimeException(chunkPosition.toString() + "这个区块坐标对应的区块为null，可能是还未加载！！！");*/
            return Blocks.ARI;
        }

        Block block = chunk.seekBlock(floor.x, floor.y);
        // 如果在当前区块找不到的话
        if (block == null) {
            Log.print(TAG, "没尽力");
        }
        // 运行到这里应该就是查找到方块了
        return block;
    }

    /**
     * 获取区块
     * @param position 世界坐标
     */
    public Chunk getChunk (Vector2 position) {
        return this.getChunk(position.x, position.y);
    }
    public Chunk getChunk (float wx, float wy) {
        return this.getChunk(this.getChunkPosition(wx, wy));
    }
    public Chunk getChunk(int chunkX, int chunkY) {
        return this.getChunk(new ChunkPosition(chunkX, chunkY));
    }
    public Chunk getChunk(ChunkPosition chunkPosition) {
        for (Chunk chunk : this.activeChunks) {
            if (chunk.getChunkPosition().equals(chunkPosition)) {
                // 如果正在活跃的区块里有这个区块则返回对应的区块
                return chunk;
            }
        }
        // 如果没有这个区块,暂时就这么处理
        Chunk chunk = this.initChunk(chunkPosition.getX(), chunkPosition.getY());
        if (chunk != null && !this.chunkExist(chunkPosition)) this._loadChunks.add(chunk);
        return chunk;
    }

    /**
     * 核心算法：
     * 获取玩家所在的区块编号
     */
    private ChunkPosition getPlayerChunkPosition(Player player) {
        Vector2 playerCenter = player.getCenter();
        return this.getChunkPosition(playerCenter.x, playerCenter.y);
    }

    public ChunkPosition getChunkPosition (Vector2 position) {
        return this.getChunkPosition(position.x, position.y);
    }
    /**
     * 获取世界坐标所对应的区块编号
     *
     * @param wx
     * @param wy
     * @return
     */
    public ChunkPosition getChunkPosition(float wx, float wy) {
        Vector2 floor = Util.fastFloor(wx, wy);
        Vector2 chunkPos = Util.fastFloor(floor.x / Chunk.ChunkWidth, floor.y / Chunk.ChunkHeight);
        return new ChunkPosition((int) chunkPos.x, (int) chunkPos.y);
    }

    /**
     * 对外开放的方法：获取玩家目前所在区块的编号（区块坐标）
     * @return
     */
    public ChunkPosition getPlayerChunkPosition() {
        return this.getPlayerChunkPosition(this.player);
    }

    /**
     * 检测所在坐标是否为区块的边缘区域
     * */
    public boolean isChunkEdge (ChunkPosition chunkPosition, float wx, float wy) {
        return !this.isChunkCenter(chunkPosition, wx, wy);
    }

    /**
     * 检测所在坐标是否为区块的中心区域
     * */
    public boolean isChunkCenter (ChunkPosition chunkPosition, float wx, float wy) {
        Rectangle chunkCenterZone = new Rectangle(
            chunkPosition.getX() + 5f,
            chunkPosition.getY() + 5f,
            6f, 6f);
        return chunkCenterZone.contains(wx, wy);
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
    private void shutdownChunkLoadPool() {
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

    /**
     * 获取区块加载器实例
     * */
    public synchronized ChunkGenerator getChunkGenerator() {
        return new MainWorldChunkGenerator(this);
    }

    public ConcurrentHashMap<String, Block> getBlockInstancesMap () {
        return this.blockInstances;
    }

    public ConcurrentHashMap<BlockWithEntity<?, ?>, BlockEntity> getBlockEntities () {
        return this.blockEntities;
    }

    /**
     * 获取方块键
     * */
    private String getBlockKey (Block block) {
        if (block instanceof BlockWithEntity blockWithEntity) {
            return blockWithEntity.getID() + ":" + blockWithEntity.hashCode();
        }
        return block.getID();
    }
}

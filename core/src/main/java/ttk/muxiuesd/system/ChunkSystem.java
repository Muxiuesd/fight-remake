package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.util.Log;
import game.muxiuesd.bedrockcore.util.Timer;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterBlockReplace;
import ttk.muxiuesd.interfaces.Tickable;
import ttk.muxiuesd.interfaces.render.IWorldChunkRender;
import ttk.muxiuesd.interfaces.render.world.block.BlockEntityRenderer;
import ttk.muxiuesd.registrant.BlockEntityRendererRegistry;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.registry.WorldInfoTypes;
import ttk.muxiuesd.render.camera.PlayerCamera;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.util.WorldMapNoise;
import ttk.muxiuesd.util.pool.PoolableRectangle;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockPos;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.block.abs.BlockEntity;
import ttk.muxiuesd.world.block.abs.BlockWithEntity;
import ttk.muxiuesd.world.block.abs.Botany;
import ttk.muxiuesd.world.block.instance.BlockAir;
import ttk.muxiuesd.world.block.instance.BlockWater;
import ttk.muxiuesd.world.chunk.Chunk;
import ttk.muxiuesd.world.chunk.ChunkLoadTask;
import ttk.muxiuesd.world.chunk.ChunkUnloadTask;
import ttk.muxiuesd.world.chunk.MainWorldChunkGenerator;
import ttk.muxiuesd.world.chunk.abs.ChunkGenerator;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.wall.Wall;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * 区块系统
 * <p>
 * 世界地图运行的核心系统，每一个区块持有方块、墙体以及植物的数据，这个系统负责统筹世界上所有的区块
 * */
public class ChunkSystem extends WorldSystem implements IWorldChunkRender {
    public final String TAG = this.getClass().getName();

    public boolean chunkEdgeRender = false;
    public boolean wallHitboxRender = false;
    public static final float Slope = 100.0f;   // 地形坡度，生成地形时的参数

    private Player player;
    private Vector2 playerLastPosition;
    private WorldMapNoise worldNoise;
    private Timer<?> chunkLoadTimer = new Timer<>(0.5f, 0.5f);

    //方块实例，不带有方块实体的同一种方块在world里只有一个实例，带有方块实体的方块都是单独一个实例
    private final ConcurrentHashMap<String, Block> blockInstances = new ConcurrentHashMap<>();
    //方块实体，每一个方块实体都是一个单独的实例
    private final ConcurrentHashMap<BlockWithEntity, BlockEntity> blockEntities = new ConcurrentHashMap<>();

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
    }

    @Override
    public void initialize () {
        Long seed = WorldInfoTypes.LONG.get(Fight.WORLD_SEED);
        this.worldNoise = new WorldMapNoise(seed);

        PlayerSystem ps = getWorld().getSystem(PlayerSystem.class);
        this.player = ps.getPlayer();
        this.playerLastPosition = new Vector2(this.player.getX() + 10000, this.player.getY() + 10000);

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
            this.addChunk(chunk);

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
                        if (!exit) this.addChunk(chunk);
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
                        this.removeChunk(chunk);
                    }
                    this.chunkUnloadingTasks.remove(position);
                }
            }
        }

        if (!this._loadChunks.isEmpty()) {
            this.activeChunks.addAll(this._loadChunks);
            this._loadChunks.clear();
        }
        //把需要被卸载的区块放进线程池
        if (!this._unloadChunks.isEmpty()) {
            for (Chunk chunk : this._unloadChunks) {
                // 生成任务并提交到线程池里卸载区块
                ChunkUnloadTask task = new ChunkUnloadTask(this, chunk);
                Future<Chunk> future = this.executor.submit(task);
                this.chunkUnloadingTasks.put(chunk.getChunkPosition(), future);
            }
            this.activeChunks.removeAll(this._unloadChunks);
        }

        // 更新正在活跃的区块
        for (Chunk chunk : this.activeChunks) {
            chunk.update(delta);
        }

        //更新方块实体
        for (BlockEntity blockEntity : this.blockEntities.values()) {
            blockEntity.update(delta);
        }

        if (this.chunkLoadTimer.isReady() && this.playerMoved()) {
            this.calculateNeedLoadedChunk();
            this.calculateNeedUnloadedChunk();
            this.playerLastPosition.set(this.player.getX(), this.player.getY());
        }
    }

    @Override
    public void draw(Batch batch) {
        //区块绘制，看不见的区块将会被剔除
        PoolableRectangle chunkEdgeRect = Pools.RECT.obtain();
        for (Chunk chunk : this.activeChunks) {
            chunkEdgeRect.set(
                chunk.getWorldX(0) - Block.WIDTH / 2f,
                chunk.getWorldY(0) - Block.HEIGHT / 2f,
                Chunk.ChunkWidth, Chunk.ChunkHeight
            );
            //判断这个区块是否可以被看见
            OrthographicCamera camera = PlayerCamera.INSTANCE.getCamera();
            //如果这个区块的边界矩形与相机视野相交，就调用区块的渲染
            if (camera.frustum.boundsInFrustum(
                chunkEdgeRect.x, chunkEdgeRect.y, 0,
                chunkEdgeRect.width, chunkEdgeRect.height, 0
            )) {
                chunk.draw(batch);
            }
        }
        Pools.RECT.free(chunkEdgeRect);

        //绘制方块实体
        this.getBlockEntities().forEach((block, blockEntity) -> {
            BlockPos pos = blockEntity.getBlockPos();
            //找对应的渲染器来执行渲染
            BlockEntityRenderer<BlockEntity> renderer = BlockEntityRendererRegistry.get(blockEntity);
            BlockEntityRenderer.Context context = renderer.getContext();
            context.x = pos.x;
            context.y = pos.y;
            renderer.render(batch, blockEntity, context);
        });
    }

    @Override
    public void batchRender (Batch batch) {
        this.draw(batch);
    }

    @Override
    public void shapeRender (ShapeRenderer shapeRenderer) {
        this.renderShape(shapeRenderer);
    }

    @Override
    public void renderShape(ShapeRenderer batch) {
        for (Chunk chunk : this.activeChunks) {
            chunk.renderShape(batch);
        }
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
     * 添加区块
     * */
    public void addChunk (Chunk chunk) {
        chunk.traversal((x, y) -> {
            //把区块里的方块实例加进系统里
            Block block = chunk.getBlock(x, y);
            this.addBlock(block, chunk.getWorldX(x), chunk.getWorldY(y));
        });
        this._loadChunks.add(chunk);
    }

    /**
     * 移除区块
     * */
    public void removeChunk (Chunk chunk) {
        chunk.traversal((x, y) -> {
            //把区块里的方块实体实例从系统里移除
            Block block = chunk.getBlock(x, y);
            if (block instanceof BlockWithEntity blockWithEntity) {
                this.removeBlockInstance(blockWithEntity);
                this.removeBlockEntity(blockWithEntity);
            }
        });
    }

    /**
     * 添加方块，确切地说，是把这个方块的实例放进区块系统里确保可以被查询到
     * */
    public void addBlock (Block block, float wx, float wy) {
        Vector2 round = Util.fastRound(wx, wy);
        //如果新的方块是带有方块实体的方块
        if (block instanceof BlockWithEntity blockWithEntity) {
            //添加方块实体
            BlockEntity blockEntity = blockWithEntity.getBlockEntity();
            if (blockEntity == null) {
                blockEntity = blockWithEntity.createBlockEntity(new BlockPos(round), getWorld());
            }
            blockWithEntity.setBlockEntity(blockEntity);
            blockEntity.setBlock(blockWithEntity);

            this.addBlockEntity(blockWithEntity, blockEntity);
            this.addBlockInstance(blockWithEntity);
            //TODO 事件：添加方块实体

        }
        else if (block instanceof Botany botany) {
            //如果是植物方块
            this.addBlockInstance(botany);
        }
        else {
            //普通方块
            this.addBlockInstance(block);
        }
    }

    /**
     * 移除方块
     * */
    public Block removeBlock (float wx, float wy) {
        Block removed = this.getBlock(wx, wy);
        Vector2 round = Util.fastRound(wx, wy);

        Chunk chunk = this.getChunk(this.getChunkPosition(round));
        GridPoint2 chunkBlockPos = Chunk.worldToChunk(round.x, round.y);
        chunk.setBlock(null, chunkBlockPos.x, chunkBlockPos.y);

        return removed;
    }


    /**
     * 向世界添加方块实例
     * <p>
     * 普通方块全世界一个实例，方块实体每一个都是单独实例，植物方块也是单独实例
     * */
    private void addBlockInstance (Block block) {
        //如果是带有方块实体的方块
        /*if (block instanceof BlockWithEntity blockWithEntity) {
            this.blockInstances.put(this.getBlockKey(blockWithEntity), blockWithEntity);
        } else if (block instanceof Botany botany) {
            this.blockInstances.put(this.getBlockKey(botany), botany);
        } else if (! this.blockInstances.containsKey(block.getID())) {
            //普通方块
            this.blockInstances.put(block.getID(), block);
        }*/

        String blockKey = this.getBlockKey(block);
        if (! this.blockInstances.containsKey(blockKey)) {
            this.blockInstances.put(blockKey, block);
        }

        //如果方快实现了tick方法就加进去执行tick更新
        if (block instanceof Tickable tickableBlock) {
            TimeSystem timeSystem = getWorld().getSystem(TimeSystem.class);
            timeSystem.add(tickableBlock);
        }
    }

    /**
     * 移除方块在这个区块系统所持有的实例
     * */
    private Block removeBlockInstance (Block block) {
        //如果方快实现了tick方法就移除它的tick更新
        if (block instanceof Tickable tickableBlock) {
            TimeSystem timeSystem = getWorld().getSystem(TimeSystem.class);
            timeSystem.remove(tickableBlock);
        }

        String blockKey = this.getBlockKey(block);
        //如果是带有方块实体的方块
        if (block instanceof BlockWithEntity blockWithEntity) {
            return this.blockInstances.remove(this.getBlockKey(blockWithEntity));
        }

        //普通方块
        if (!this.blockInstances.containsKey(blockKey)) {
            throw new IllegalArgumentException("方块：" + blockKey + " 的实例从未添加过！！！");
        }

        return this.blockInstances.remove(blockKey);
    }

    /**
     * 添加方块实体
     * */
    private void addBlockEntity(BlockWithEntity block, BlockEntity blockEntity) {
        if (block == null || blockEntity == null) return;

        TimeSystem ts = getWorld().getSystem(TimeSystem.class);
        ts.add(blockEntity);
        this.getBlockEntities().put(block, blockEntity);

        blockEntity.setWorld(getWorld());
        blockEntity.bePlaced(getWorld(), this.player);
    }

    /**
     * 移除方块实体
     * */
    private BlockEntity removeBlockEntity (BlockWithEntity block) {
        BlockEntity removed = this.getBlockEntities().remove(block);
        if (removed == null) return null;

        TimeSystem ts = getWorld().getSystem(TimeSystem.class);
        ts.remove(removed);
        removed.beDestroyed(getWorld(), this.player);

        return removed;
    }

    /**
     * 替换某个位置的方块
     * @param position 世界坐标
     * */
    public Block replaceBlock (Block newBlock, Vector2 position) {
        return this.replaceBlock(newBlock, position.x, position.y);
    }
    /**
     * 替换某个位置的方块
     * @return 被替换下来的方块
     * */
    public Block replaceBlock (Block newBlock, float wx, float wy) {
        if (newBlock == null) {
            throw new NullPointerException("newBlock 不能为null！！！");
        }
        Block oldBlock = this.getBlock(wx, wy);
        Vector2 round = Util.fastRound(wx, wy);

        ChunkPosition chunkPosition = this.getChunkPosition(round);
        Chunk chunk = this.getChunk(chunkPosition);
        GridPoint2 chunkBlockPos = Chunk.worldToChunk(round.x, round.y);

        //如果旧的方块是带有方块实体的方块
        if (oldBlock instanceof BlockWithEntity blockWithEntity) {
            this.removeBlockEntity(blockWithEntity);
            //TODO 事件：移除方块实体
            //移除对应的方块实例
            this.removeBlockInstance(oldBlock);
        }

        //如果新方块是带有方块实体的方块
        if (newBlock instanceof BlockWithEntity blockWithEntity) {
            //需要新建一个实例再添加
            BlockWithEntity self = blockWithEntity.createSelf();
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
     * 放置墙体
     * @return 放置成功返回true，否则为false
     * */
    public boolean placeWall (Wall<?> wall, float wx, float wy) {
        //本来就有墙体就不能放置
        if (this.getWall(wx, wy) != null) return false;

        Block block = this.getBlock(wx, wy);
        //在空气方块或者水方块上不得放置墙体
        if (block instanceof BlockAir || block instanceof BlockWater) return false;

        Vector2 round = Util.fastRound(wx, wy);
        //每一个墙体都是一个单独的实例
        Wall<?> instance = wall.createSelf(round);
        Chunk chunk = this.getChunk(this.getChunkPosition(round));
        GridPoint2 chunkWallPos = Chunk.worldToChunk(round.x, round.y);
        chunk.setWall(instance, chunkWallPos.x, chunkWallPos.y);

        System.out.println("在区块：" + chunk.getChunkPosition().toString()
            + " 的" + chunkWallPos.toString() + "上放置墙体");

        return true;
    }

    /**
     * 破坏墙体
     * @return 被破坏的墙体实例
     * */
    public Wall<?> destroyWall (Vector2 position) {
        return this.destroyWall(position.x, position.y);
    }
    public Wall<?> destroyWall (float wx, float wy) {
        //没有墙体
        if (this.getWall(wx, wy) == null) return null;
        Vector2 round = Util.fastRound(wx, wy);
        Chunk chunk = this.getChunk(this.getChunkPosition(round));
        GridPoint2 cp = Chunk.worldToChunk(round.x, round.y);
        Wall<?> wall = chunk.getWall(cp.x, cp.y);
        chunk.setWall(null, cp.x, cp.y);
        return wall;
    }

    /**
     * 放置植物
     * <p>
     * 当对应坐标上没有其他植物时就放置此植物
     * */
    public void placeBotany (Botany botany, float wx, float wy) {
        Chunk chunk = this.getChunk(wx, wy);
        Vector2 roundPos = Util.fastRound(wx, wy);
        GridPoint2 chunkPos = Chunk.worldToChunk(roundPos.x, roundPos.y);
        //如果这个坐标上没有其他植物就可以放置
        if (!chunk.hasBotany(chunkPos.x, chunkPos.y)) {
            //创建新的植物实例并添加
            Botany self = botany.createSelf();
            this.addBlock(self, wx, wy);
            chunk.setBotany(self, chunkPos.x, chunkPos.y);
        }
    }

    /**
     * 破坏植物
     * @param position 世界坐标
     * */
    public Botany destroyBotany (Vector2 position) {
        return this.destroyBotany(position.x, position.y);
    }
    /**
     * 破坏植物
     * @return 返回被破坏的植物的实例
     * */
    public Botany destroyBotany (float wx, float wy) {
        Chunk chunk = this.getChunk(wx, wy);
        Vector2 roundPos = Util.fastRound(wx, wy);
        GridPoint2 chunkPos = Chunk.worldToChunk(roundPos.x, roundPos.y);

        if (chunk.hasBotany(chunkPos.x, chunkPos.y)) {
            //有植物就破坏，并且返回被破坏的植物实例
            Botany botanyInstance = chunk.getBotany(chunkPos.x, chunkPos.y);
            //记得移除这个植物的实例
            this.removeBlockInstance(botanyInstance);
            chunk.setBotany(null, chunkPos.x, chunkPos.y);
            //调用被破坏方法
            botanyInstance.beDestroyed(getWorld(), roundPos);
            return botanyInstance;
        }

        return null;
    }



    /**
     * 计算需要被加载的区块
     */
    private void calculateNeedLoadedChunk() {
        // 计算玩家所在区块坐标编号
        ChunkPosition chunkPosition = this.getPlayerChunkPosition(this.player);
        int playerChunkX = chunkPosition.getX();
        int playerChunkY = chunkPosition.getY();
        Vector2 playerCenter = this.player.getCenterPos();

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
            float distance = Util.getDistance(this.player.getX(), this.player.getY(),
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
     * @return 方块
     */
    public Block getBlock (float wx, float wy) {
        Vector2 round = Util.fastRound(wx, wy);
        ChunkPosition chunkPosition = this.getChunkPosition(round);

        Chunk chunk = this.getChunk(chunkPosition);
        if (chunk == null) {
            return Blocks.ARI;
        }

        Block block = chunk.seekBlock(round.x, round.y);
        // 如果在当前区块找不到的话
        if (block == null) {
            Log.print(TAG, "没尽力");
        }
        // 运行到这里应该就是查找到方块了
        return block;
    }

    /**
     * 获取墙体
     * @param position 世界坐标
     * @return 有墙体就返回对应的实例，没有就返回null
     * */
    public Wall<?> getWall (Vector2 position) {
        return this.getWall(position.x, position.y);
    }
    public Wall<?> getWall (float wx, float wy) {
        Vector2 round = Util.fastRound(wx, wy);
        Chunk chunk = this.getChunk(this.getChunkPosition(round));
        if (chunk == null) return null;

        return chunk.seekWall(round.x, round.y);
    }

    /**
     * 获取植物
     * @param position 世界坐标
     * */
    public Botany getBotany (Vector2 position) {
        return this.getBotany(position.x, position.y);
    }
    /**
     * 获取植物
     * @param wx 世界x坐标
     * @param wy 世界y坐标
     * @return 植物
     * */
    public Botany getBotany (float wx, float wy) {
        Vector2 round = Util.fastRound(wx, wy);
        Chunk chunk = this.getChunk(this.getChunkPosition(round));
        if (chunk == null) return null;

        return chunk.seekBotany(round.x, round.y);
    }

    /**
     * 获取区块
     * @param position 世界坐标
     */
    public Chunk getChunk (Vector2 position) {
        return this.getChunk(position.x, position.y);
    }
    public Chunk getChunk (float wx, float wy) {
        return this.getChunk(this.getChunkPos(wx, wy));
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
        Vector2 playerCenter = player.getCenterPos();
        return this.getChunkPos(playerCenter.x, playerCenter.y);
    }

    /**
     * 获取世界坐标所对应的区块编号
     * @param position 世界坐标
     * */
    public ChunkPosition getChunkPos (Vector2 position) {
        return this.getChunkPos(position.x, position.y);
    }
    /**
     * 获取世界坐标所对应的区块编号
     * @param wx
     * @param wy
     * @return
     */
    public ChunkPosition getChunkPos (float wx, float wy) {
        return this.getChunkPosition(Util.fastRound(wx, wy));
    }

    /**
     * 获取世界坐标所对应的区块编号
     * @param roundPosition 已经四舍五入过的世界坐标
     */
    public ChunkPosition getChunkPosition (Vector2 roundPosition) {
        ///这里就该这么写，最好别动
        Vector2 chunkPos = Util.fastFloor(roundPosition.x / Chunk.ChunkWidth, roundPosition.y / Chunk.ChunkHeight);
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

    public WorldMapNoise getWorldNoise () {
        return this.worldNoise;
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

    public ConcurrentHashMap<BlockWithEntity, BlockEntity> getBlockEntities () {
        return this.blockEntities;
    }

    /**
     * 获取方块键（或者叫做方块实例键）
     * <p>
     * 对于一个方块对应一个实例的方块来说，方快类型不同，键的类型就不同。普通方块全世界一个共享的实例，就直接用方快id当键
     * <p>
     * TODO 多种类型的方快的方快键的判断
     * */
    private String getBlockKey (Block block) {
        if (block instanceof BlockWithEntity blockWithEntity) {
            return blockWithEntity.getID() + "@" + blockWithEntity.hashCode();
        }
        if (block instanceof Botany botany) {
            return botany.getID() + "@" + botany.hashCode();
        }
        return block.getID();
    }
}

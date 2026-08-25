package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import game.muxiuesd.bedrockcore.util.Log;
import game.muxiuesd.bedrockcore.util.UnifiedFileUtil;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterEntityDeath;
import ttk.muxiuesd.interfaces.Tickable;
import ttk.muxiuesd.interfaces.render.IWorldGroundEntityRender;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.interfaces.world.entity.PoolableEntity;
import ttk.muxiuesd.key.KeyBindings;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.EntityTypes;
import ttk.muxiuesd.registry.RenderLayers;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.registry.WorldInfoTypes;
import ttk.muxiuesd.render.RenderLayer;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.ChunkPosition;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.chunk.Chunk;
import ttk.muxiuesd.world.entity.EntityLoadTask;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.EntityUnloadTask;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.player.Player;
import ttk.muxiuesd.world.hitbox.Hitbox;
import ttk.muxiuesd.world.hitbox.RectHitbox;
import ttk.muxiuesd.world.item.ItemPickUpState;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.HashMap;
import java.util.concurrent.*;

/**
 * 实体的管理系统，负责实体的储存以及更新，但不负责渲染
 * */
public class EntitySystem extends WorldSystem implements IWorldGroundEntityRender, Tickable {
    public static final float MIN_SPEED = 0.0000001f;
    private boolean renderHitbox = false;

    private final Array<Entity<?>> _delayAdd = new Array<>();
    private final Array<Entity<?>> _delayRemove = new Array<>();

    private final Array<Entity<?>> entities = new Array<>();   //所有实体
    private final Array<Entity<?>> updatableEntity = new Array<>(); //可以更新的实体
    private final Array<Entity<?>> incationEntity = new Array<>(); //不更新的实体

    //实体管理组map，每一种注册过的实体类型都有一个管理组，key为实体类型，value为该实体类型的持有实体管理数组
    private final ConcurrentHashMap<EntityType<?>, Array<? extends Entity<?>>> entityTypes = new ConcurrentHashMap<>();

    //可渲染的实体组map，key为渲染层级，value为该层级下所有要渲染的实体
    private final ConcurrentHashMap<RenderLayer, Array<Entity<?>>> renderableEntities = new ConcurrentHashMap<>();

    // 线程池
    private ExecutorService executor;
    private ConcurrentHashMap<ChunkPosition, Future<Array<Entity<?>>>> entityLoadingTasks;
    private ConcurrentHashMap<ChunkPosition, Future<Array<Entity<?>>>> entityUnloadingTasks;


    public EntitySystem (World world) {
        super(world);
        WorldInfoTypes.FLOAT.putIfNull(Fight.ENTITY_UPDATE_RANGE);
        WorldInfoTypes.FLOAT.putIfNull(Fight.ENTITY_RENDER_RANGE);
        WorldInfoTypes.FLOAT.putIfNull(Fight.ITEM_ENTITY_PICKUP_SPAN);
        WorldInfoTypes.FLOAT.putIfNull(Fight.MAX_ITEM_ENTITY_LIVING_TIME);
    }

    @Override
    public void initialize () {
        //将注册的实体类型的管理组加进来
        for (EntityType<?> entityType : Registries.ENTITY_TYPE.getMap().values()) {
            this.entityTypes.put(entityType, entityType.createEntityArray());
        }

        //把玩家实体加进来
        PlayerSystem ps = getManager().getSystem(PlayerSystem.class);
        Player player = ps.getPlayer();
        player.setEntitySystem(this);
        this.add(player);

        this.renderableEntities.put(RenderLayers.ENTITY_UNDERGROUND, new Array<>());
        this.renderableEntities.put(RenderLayers.ENTITY_GROUND, new Array<>());

        this.initPool();

        //添加tick任务
        TimeSystem timeSystem = getManager().getSystem(TimeSystem.class);
        timeSystem.add(this);

        Log.print(TAG(), "EntitySystem初始化完成！");
    }

    /**
     * 初始化线程池
     */
    private void initPool () {
        int coreSize = Runtime.getRuntime().availableProcessors();
        Log.print(TAG(), "初始化实体加载卸载线程池，核心线程数：" + coreSize);
        this.executor = Executors.newFixedThreadPool(coreSize);
        this.entityLoadingTasks = new ConcurrentHashMap<>();
        this.entityUnloadingTasks = new ConcurrentHashMap<>();
    }

    /**
     * 添加实体
     * */
    public <T extends Entity> void add (T entity) {
        //防止重复添加
        if (this._delayAdd.contains(entity, true) || this.entities.contains(entity, true)) return;

        this._delayAdd.add(entity);
    }

    /**
     * 移除实体
     * */
    public <T extends Entity> void remove (T entity) {
        //防止重复移除：实体已不在系统中，或已在移除队列中，直接跳过
        if (this.entities.contains(entity, true)
            && !this._delayRemove.contains(entity, true)) {
            this._delayRemove.add(entity);
        }
    }

    /**
     * 延迟添加实体防止并发修改异常
     * @param entity 实体
     */
    private <T extends Entity> void _add (T entity) {
        Array<T> entityArray = (Array<T>) this.getEntityArray(entity.getType());
        if (! entityArray.contains(entity, true)) {
            //避免重复添加
            entityArray.add(entity);
        }
        this.entities.add(entity);
        //把实体添加进相应的渲染层级
        if (this.renderableEntities.containsKey(entity.getRenderLayer()))
            this.renderableEntities.get(entity.getRenderLayer()).add(entity);
        //防止没有指定实体系统
        entity.setEntitySystem(this);
        entity.lazyInitialize();
    }

    /**
     * 延迟移除实体防止并发修改异常
     * @param entity 实体
     */
    private <T extends Entity> void _remove (T entity) {
        //幂等防御：实体已不在系统中则跳过（防止重复移除导致池化对象 double-free）
        if (!this.entities.contains(entity, true)) return;

        Array<T> entityArray = (Array<T>) this.getEntityArray(entity.getType());
        entityArray.removeValue(entity, true);

        this.entities.removeValue(entity, true);
        this.updatableEntity.removeValue(entity, true);
        this.incationEntity.removeValue(entity, true);
        //把实体移除出渲染层级
        Array<Entity<?>> renderLayerEntities = this.renderableEntities.get(entity.getRenderLayer());
        if (renderLayerEntities != null) renderLayerEntities.removeValue(entity, true);
        //执行池化实体释放逻辑
        if (entity instanceof PoolableEntity poolableEntity) {
            poolableEntity.freeSelf();
        }
        //释放实体自己的资源
        entity.dispose();
    }

    @Override
    public void update(float delta) {
        if (KeyBindings.HitboxDisplay.wasJustPressed()) this.renderHitbox = !this.renderHitbox;

        if (!_delayRemove.isEmpty()) {
            for (Entity entity : this._delayRemove) {
                _remove(entity);
            }
            _delayRemove.clear();
        }
        if (!_delayAdd.isEmpty()) {
            for (Entity entity : this._delayAdd) {
                _add(entity);
            }
            _delayAdd.clear();
        }

        this.updateEntities(delta);
    }

    @Override
    public void tick (World world, float delta) {
        //每一个可更新实体的tick更新
        this.updatableEntity.forEach(entity -> entity.tick(world, delta));

        this.calculateNeedActiveEntity();
        this.calculateInactionEntity();

        this.checkTasks();
    }

    /**
     * 更新所有可更新实体的总入口方法
     * */
    private void updateEntities (float delta) {
        //先把所有实体更新一次
        for (Entity entity : this.updatableEntity) {
            //细化实体更新
            //对于活物实体
            if (entity instanceof LivingEntity livingEntity) {
                this.updateLivingEntity(livingEntity, delta);
            }
            //对于物品实体
            else if (entity instanceof ItemEntity itemEntity) {
                this.updateItemEntity(itemEntity, delta);
            }

            entity.update(delta);

            //状态机/玩家输入等"意图"写入之后再统一应用方块摩擦（击退中的实体衰减由击退物理负责）
            if (!entity.isKnockback()) {
                this.calculateEntityCurSpeed(entity, getManager().getSystem(ChunkSystem.class), delta);
            }
        }
    }

    private void updateLivingEntity(LivingEntity livingEntity, float delta) {
        //移除死亡的实体,玩家死亡移除不在这个逻辑里
        if (livingEntity.isDeath()) {
            EventBus.post(EventTypes.ENTITY_DEATH, new EventPosterEntityDeath(getWorld(), livingEntity));
            this.remove(livingEntity);
        }
    }

    /**
     * 更新物品实体相关的东西
     * */
    private void updateItemEntity (ItemEntity itemEntity, float delta) {
        //移除超过存活时间的物品实体
        if (itemEntity.getLivingTime() > Fight.MAX_ITEM_ENTITY_LIVING_TIME.getValue()) {
            this.remove(itemEntity);
            //跳过这个物品实体的 其他操作
            return;
        }
        Player player = this.getPlayer();
        //需要被丢弃物品实体存在时间超过三秒，防止一丢弃就被自动捡回来
        if (itemEntity.getLivingTime() > Fight.ITEM_ENTITY_PICKUP_SPAN.getValue()) {
            //当物品实体与玩家的碰撞箱相碰就是捡起
            if (itemEntity.getBodyHitbox().checkCollision(player.getBodyHitbox())) {
                ItemStack itemStack = itemEntity.getItemStack();
                ItemPickUpState state = player.pickUpItem(itemStack);
                if (state == ItemPickUpState.WHOLE) {
                    this.remove(itemEntity);
                    getManager().getSystem(SoundSystem.class).playSpatialSound(Sounds.ITEM_POP, player);
                    //整个捡起来就没必要执行下面的代码了
                    return;
                }else if (state == ItemPickUpState.PARTIAL) {
                    //部分捡起时刷新存在时间
                    itemEntity.setLivingTime(0);
                }
                //捡起失败则什么也没发生（速度与基准速度都清零，防止残留速度）
                itemEntity
                    .setBeingAttracted(false)
                    .setOnGround(true)
                    .setVelocity(0, 0)
                    .setCurSpeed(0);
            }

            float distance = Util.getDistance(itemEntity, player);
            //物品实体是否在玩家实体的吸引范围内
            boolean inRange = distance <= Fight.PLAYER_PICKUP_RANGE.getValue()
                && !player.getBackpack().isFull(itemEntity.getItemStack());
            if (inRange) {
                //在捡起范围内，并且对于这个物品来说背包还没满，让物品实体朝向玩家运动
                Direction direction = new Direction(itemEntity.getCenterPos(), player.getCenterPos());
                //吸引速度随距离衰减：远处快、近处慢（避免物品在玩家周围环绕抖动）
                float speed = Math.min(12f, distance * 4f);
                itemEntity
                    .setBeingAttracted(true)
                    .setOnGround(false)
                    .setVelocity(direction.getX(), direction.getY())
                    .setCurSpeed(speed);
            } else if (itemEntity.isBeingAttracted()) {
                //离开了吸引范围：停止朝向玩家移动（不再保留残留速度）
                itemEntity
                    .setBeingAttracted(false)
                    .setOnGround(true)
                    .setVelocity(0, 0)
                    .setCurSpeed(0);
            }
        }

        //this.calculateEntityCurSpeed(itemEntity, getManager().getSystem(ChunkSystem.class), delta);
    }

    /// 无意图实体摩擦衰减的参考帧率：等效"该帧率下运行帧级衰减"的表现（数值与运行帧率无关，delta 自动补偿）
    private static final float REFERENCE_FPS = 60f;

    /**
     * 对实体进行当前速度大小的计算（不改变方向）
     * <p>
     * 意图实体（{@link Entity#hasIntent()}）：每帧重置意图 → 帧级缩放（稳定速度 = 意图 × (1-摩擦)，帧率无关）；
     * 无意图实体（物品等）：秒级衰减 ×(1-摩擦)^(delta×参考帧率)（等效参考帧率下的帧级表现，帧率无关、快速停止）。
     * 空气阻力（秒级）对所有实体生效；击退中实体由击退物理负责，跳过本方法
     */
    private void calculateEntityCurSpeed (Entity entity, ChunkSystem cs, float delta) {
        //对于速度为0的实体不进行速度更新
        if (entity.getSpeed() <= 0 && entity.getCurSpeed() <= 0) return;

        float airDrag = (float) Math.pow(1f - Fight.AIR_FRICTION.getValue(), delta);    //空气阻力的影响（秒级）

        //子弹实体不受方块摩擦力影响，只受空气阻力
        if (entity instanceof Bullet bullet) {
            bullet.setVelX(bullet.getVelX() * airDrag);
            bullet.setVelY(bullet.getVelY() * airDrag);
            return;
        }

        float curSpeed = entity.getCurSpeed();

        //如果实体在地面上
        if (entity.isOnGround()) {
            //计算脚下方块摩擦对速度的影响（取样点与游泳判定一致，都用实体底部）
            Block block = cs.getBlock(entity.getX(), entity.getY() - entity.getHeight() / 2f);
            if (block != null) {
                float scale = Math.max(0f, 1f - block.getProperty().getFriction());
                if (entity.hasIntent()) {
                    //意图实体：帧级缩放（意图每帧重置 → 稳定速度 = 意图 × scale，帧率无关）
                    curSpeed *= scale;
                } else {
                    //无意图实体：秒级衰减（等效参考帧率下的帧级表现，帧率无关）
                    curSpeed *= (float) Math.pow(scale, delta * REFERENCE_FPS);
                }
            }
        }

        //速度过小直接为0
        if (curSpeed < MIN_SPEED) {
            entity.setCurSpeed(0f);
            return;
        }

        entity.setCurSpeed(curSpeed);
        //应用空气阻力
        entity.setVelocity(entity.getVelX() * airDrag, entity.getVelY() * airDrag);

        if (entity == getPlayer()) {
            Log.print(TAG(), "玩家当前速度：" + entity.getCurSpeed());
        }
    }

    /**
     * 计算需要更新的实体（活跃实体）
     * */
    private void calculateNeedActiveEntity() {
        Player player = this.getPlayer();
        if (player == null) return;
        for (Entity<?> entity: this.getEntities()) {
            float distance = Util.getDistance(entity, player);
            if (distance <= Fight.ENTITY_UPDATE_RANGE.getValue()) {
                this.activateEntity(entity);
            }
        }
    }

    /**
     * 计算不需要更新的实体（不活跃实体）
     * */
    private void calculateInactionEntity() {
        Player player = this.getPlayer();
        if (player == null) return;
        for (Entity<?> entity: this.getEntities()) {
            float distance = Util.getDistance(entity, player);
            if (distance > Fight.ENTITY_UPDATE_RANGE.getValue()) {
                this.deactivateEntity(entity);
            }
        }
    }

    /**
     * 激活实体
     * */
    private void activateEntity (Entity<?> entity) {
        if (!this.updatableEntity.contains(entity, true)) {
            this.updatableEntity.add(entity);
        }
        if (this.incationEntity.contains(entity, true)) {
            this.incationEntity.removeValue(entity, true);
        }
    }

    /**
     * 静默实体
     * */
    private void deactivateEntity (Entity<?> entity) {
        if (!this.incationEntity.contains(entity, true)) {
            this.incationEntity.add(entity);
        }
        if (this.updatableEntity.contains(entity, true)) {
            this.updatableEntity.removeValue(entity, true);
        }
    }



    /**
     * 检查多线程的任务
     * */
    private void checkTasks () {
        //检查卸载任务
        if (! this.entityUnloadingTasks.isEmpty()) {
            for (ChunkPosition chunkPosition : this.entityUnloadingTasks.keySet()) {
                Future<Array<Entity<?>>> future = this.entityUnloadingTasks.get(chunkPosition);
                if (future != null && future.isDone()) {
                    this.entityUnloadingTasks.remove(chunkPosition);
                    try {
                        Array<Entity<?>> unloaded = future.get();
                        //卸载保存完成，将实体从系统中移除，避免实体残留导致重复加载（实体翻倍）和内存泄漏
                        if (unloaded != null) {
                            for (Entity<?> entity : unloaded) {
                                this.remove(entity);
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        //保存失败，实体保留在系统中，防止数据丢失
                        e.printStackTrace();
                    }
                }
            }
        }

        //检查加载任务
        if (! this.entityLoadingTasks.isEmpty()) {
            for (ChunkPosition chunkPosition : this.entityLoadingTasks.keySet()) {
                Future<Array<Entity<?>>> future = this.entityLoadingTasks.get(chunkPosition);
                if (future != null && future.isDone()) {
                    try {
                        Array<Entity<?>> entityArray = future.get();
                        //添加到延迟队列
                        this._delayAdd.addAll(entityArray);
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                    this.entityLoadingTasks.remove(chunkPosition);
                }
            }
        }
    }

    /**
     * 卸载某一区块内的实体
     * @param chunk 需要被卸载的区块
     * */
    public void unloadEntities (ChunkSystem cs, Chunk chunk) {
        Array<Entity<?>> copy = new Array<>(this.getIncationEntity()); //复制一份
        Array<Entity<?>> unload = new Array<>();    //需要被卸载的实体组

        for (Entity<?> entity: copy) {
            ChunkPosition chunkPosition = cs.getChunkPos(entity.getCenterPos());
            EntityProvider<?> entityProvider = entity.getProvider();
            //检查实体所在区块是否为传入的需要被卸载的区块，同时需要实体能够被保存
            if (chunkPosition.equals(chunk.getChunkPosition())
                && entityProvider.canBeSaved) {
                unload.add(entity);
            }
        }

        if (unload.isEmpty()) return;

        unload.removeValue(this.getPlayer(), true);

        //该区块已有卸载任务在途：跳过本次提交。
        //实体仍留在系统中，等原任务完成（实体被移除）后，下次卸载时会被重新收集保存；
        //若此时覆盖提交，旧 Future 的完成回调会丢失，导致已收集的实体永不移除。
        if (this.entityUnloadingTasks.containsKey(chunk.getChunkPosition())) {
            return;
        }

        EntityUnloadTask unloadTask = new EntityUnloadTask(this, unload, chunk.getChunkPosition());
        Future<Array<Entity<?>>> submit = this.executor.submit(unloadTask);
        this.entityUnloadingTasks.put(chunk.getChunkPosition(), submit);
    }

    /**
     * 卸载所有实体
     * */
    public void unloadAllEntities () {
        HashMap<String, ChunkPosition> chunkPos = new HashMap<>();
        HashMap<ChunkPosition, Array<Entity<?>>> unloadArray = new HashMap<>();
        //复制一份，避免直接修改内部实体数组
        Array<Entity<?>> allEntities = new Array<>(this.getEntities());
        //单独去除玩家实体
        allEntities.removeValue(this.getPlayer(), true);
        ChunkSystem chunkSystem = getWorld().getSystem(ChunkSystem.class);

        for (Entity<?> entity: allEntities) {
            EntityProvider<?> entityProvider = entity.getProvider();
            if (!entityProvider.canBeSaved) continue;

            Vector2 position = entity.getCenterPos();
            ChunkPosition chunkPosition = chunkSystem.getChunkPos(position.x, position.y);
            String name = chunkPosition.toString();
            //没有就新建一个
            if (!chunkPos.containsKey(name)) {
                chunkPos.put(name, chunkPosition);
                unloadArray.put(chunkPosition, new Array<>());
            }
            //添加进对应区块的实体组
            unloadArray.get(chunkPos.get(name)).add(entity);
        }

        //对每一个区块的实体组进行卸载任务
        unloadArray.forEach((chunkPosition, array) -> {
            new EntityUnloadTask(this, array, chunkPosition).call();
        });

        Log.print(TAG(), "全部实体的数据保存完成。");
    }

    /**
     * 加载对应区块上的所有实体
     * */
    public void loadEntities (ChunkSystem cs, Chunk chunk) {
        ChunkPosition chunkPosition = chunk.getChunkPosition();
        //该区块实体正在被卸载保存（写盘）中，跳过本次加载，避免读到半截文件；下次区块重新加载时会再尝试
        if (this.entityUnloadingTasks.containsKey(chunkPosition)) {
            return;
        }
        //文件不存在就是区块上没有实体，直接跳过
        if (! UnifiedFileUtil.fileExists(Fight.getPathSaveEntities(), chunkPosition.toString() + ".json")) {
            return;
        }
        EntityLoadTask loadTask = new EntityLoadTask(this, chunkPosition);
        Future<Array<Entity<?>>> submit = this.executor.submit(loadTask);
        this.entityLoadingTasks.put(chunkPosition, submit);
    }

    /**
     * （主线程）初始化加载实体
     * */
    public void initLoadEntities (ChunkPosition chunkPosition) {
        String fileName = chunkPosition.toString() + ".json";
        //没有实体数据就跳过
        if (! UnifiedFileUtil.fileExists(Fight.getPathSaveEntities(), fileName)) return;

        EntityLoadTask loadTask = new EntityLoadTask(this, chunkPosition);
        Array<Entity<?>> entities = loadTask.call();
        this._delayAdd.addAll(entities);

        UnifiedFileUtil.deleteFile(Fight.getPathSaveEntities(), fileName);
    }

    @Override
    public void batchRender (Batch batch) {
    }

    @Override
    public void shapeRender (ShapeRenderer shapeRenderer) {
        this.renderShape(shapeRenderer);
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        if (this.renderHitbox) {
            for (Entity entity : this.entities) {
                Hitbox hitbox = entity.getBodyHitbox();
                if (hitbox instanceof RectHitbox rectHitbox){
                    Rectangle box = rectHitbox.getRectangle();
                    batch.rect(box.x, box.y, box.width, box.height);
                }
                Vector2 entityCenter = entity.getCenterPos();
                if (entity instanceof LivingEntity livingEntity) {
                    batch.line(entityCenter, new Vector2(entityCenter).add(livingEntity.getDirection().toVector2()));
                }else {
                    batch.line(entityCenter, new Vector2(entityCenter).add(entity.getVelocity().scl(1)));
                }
            }
        }
    }

    @Override
    public void dispose() {
        this.unloadAllEntities();
        this.shutdownPool();
    }

    /**
     * 关闭线程池
     */
    private void shutdownPool () {
        this.executor.shutdown();
        try {
            if (!this.executor.awaitTermination(1, TimeUnit.SECONDS)) {
                this.executor.shutdownNow();
                Log.print(TAG(), "实体加载卸载任务线程池关闭");
            }
        } catch (InterruptedException e) {
            this.executor.shutdownNow();
            throw new RuntimeException(e);
        }
    }


    public Player getPlayer() {
        PlayerSystem ps = getManager().getSystem(PlayerSystem.class);
        return ps.getPlayer();
    }

    /**
     * 获取所有的实体
     * */
    public Array<Entity<?>> getEntities () {
        return this.entities;
    }

    /**
     * 获取不活跃的实体
     * */
    public Array<Entity<?>> getIncationEntity () {
        return this.incationEntity;
    }

    /**
     * 获取实体类型相应的管理组
     * */
    public <T extends Entity<?>> Array<T> getEntityArray (EntityType<? extends T> type) {
        return (Array<T>) this.entityTypes.get(type);
    }

    public Array<Enemy<?>> getEnemyEntity () {
        return this.getEntityArray(EntityTypes.ENEMY);
    }

    public Array<Bullet<?>> getPlayerBulletEntity () {
        return this.getEntityArray(EntityTypes.PLAYER_BULLET);
    }

    public Array<Bullet<?>> getEnemyBulletEntity () {
        return this.getEntityArray(EntityTypes.ENEMY_BULLET);
    }

    /**
     * 获取所有可渲染实体
     * */
    public ConcurrentHashMap<RenderLayer, Array<Entity<?>>> getRenderableEntities () {
        return this.renderableEntities;
    }

    @Override
    public int getRenderPriority () {
        return 10000;
    }
}



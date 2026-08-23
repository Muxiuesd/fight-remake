package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.DataResult;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import game.muxiuesd.bedrockcore.util.TaskTimer;
import ttk.muxiuesd.interfaces.world.entity.PoolableEntity;
import ttk.muxiuesd.registry.EntityTypes;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.serialization.codecs.builders.EntityCodecBuilder;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.cat.CatBoolean;
import ttk.muxiuesd.world.cat.CatFloat;
import ttk.muxiuesd.world.cat.CatsHolder;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.item.ItemStack;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 物品实体
 * <p>
 * 掉落在地上的物品以实体形式存在
 * */
public class ItemEntity extends Entity<ItemEntity> implements Pool.Poolable, PoolableEntity {
    public static final Vector2 DEFAULT_SIZE = new Vector2(0.5f, 0.5f);

    /**
     * 空中计时器的编解码器
     * <p>
     * 有计时器时编码为 {active: true, max_span, cur_span}，没有时编码为 {active: false}
     */
    private static final Codec<TaskTimer> ON_AIR_TIMER_CODEC = new Codec<>() {
        @Override
        public RawObject encode (TaskTimer timer) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("active", timer != null);
            if (timer != null) {
                map.put("max_span", Codec.FLOAT.encode(timer.getMaxSpan()).unwrap());
                map.put("cur_span", Codec.FLOAT.encode(timer.getCurSpan()).unwrap());
            }
            return RawObject.ofMap(map);
        }

        @Override
        public DataResult<TaskTimer> decode (RawObject input) {
            if (!input.isMap()) return DataResult.error("Expected a map");
            Map<String, Object> rawMap = input.asMap().get();
            boolean active = Codec.BOOL.decode(Codec.wrap(rawMap.get("active"))).result().orElse(false);
            if (!active) return DataResult.success(null);
            float maxSpan = Codec.FLOAT.decode(Codec.wrap(rawMap.get("max_span"))).result().orElse(0f);
            float curSpan = Codec.FLOAT.decode(Codec.wrap(rawMap.get("cur_span"))).result().orElse(0f);
            return DataResult.success(new TaskTimer(maxSpan, curSpan, () -> {}));
        }
    };

    /**
     * 物品实体的现代化编解码器
     * <p>
     * 解码时通过实体注册表创建实例，编码时编码基础实体与物品实体的全部字段
     */
    public static final Codec<ItemEntity> CODEC = EntityCodecBuilder.<ItemEntity>create()
        .field("itemStack", ItemEntity::getItemStack, ItemEntity::setItemStack, ItemStack.CODEC)
        .field("livingTime", ItemEntity::getLivingTime, ItemEntity::setLivingTime, Codec.FLOAT)
        .field("cycle", ItemEntity::getCycle, ItemEntity::setCycle, Codec.FLOAT)
        .field("onAirTimer", ItemEntity::getOnAirTimer, ItemEntity::setOnAirTimerFromCodec, ON_AIR_TIMER_CODEC)
        .factory(EntityCodecBuilder::createEntity);

    private ItemStack itemStack = ItemStack.VOID;
    private Vector2 positionOffset;
    private TaskTimer onAirTimer;   //在空中的计时器，可以自定义物品实体在空中运动的时间
    private float cycle;
    private float livingTime;   //存在时间
    private boolean beingAttracted;  //是否正在被玩家吸引（吸引期间不受方块摩擦力影响）

    public ItemEntity (World world, EntityType<? super ItemEntity> entityType) {
        this(world);
    }
    public ItemEntity (World world) {
        super(world, EntityTypes.ITEM_ENTITY);
        this.positionOffset = new Vector2();
        setSize(DEFAULT_SIZE);
        fastAddBodyHitBox();
    }

    /**
     * 物品实体是否被吸引
     * */
    public boolean isBeingAttracted () {
        return this.beingAttracted;
    }

    /**
     * 设置物品实体是否被吸引
     * */
    public ItemEntity setBeingAttracted (boolean beingAttracted) {
        this.beingAttracted = beingAttracted;
        return this;
    }

    @Override
    public void readCatData (JsonValue values) {
        super.readCatData(values);
        this.cycle = values.getFloat("cycle", 0f);
        this.livingTime = values.getFloat("livingTime", 0f);


        if (values.has("on_air")) {
            if (values.getBoolean("on_air")) {
                this.onAirTimer = new TaskTimer(
                    values.getFloat("on_air_max_span", 0f),
                    values.getFloat("on_air_cur_span", 0f),
                    () -> this.setOnAirTimer(null)
                );
            }
        }
    }

    @Override
    public void writeCatData (CatsHolder holder) {
        super.writeCatData(holder);
        holder
            .put("cycle", new CatFloat(this.cycle))
            .put("livingTime", new CatFloat(this.livingTime));

        if (this.onAirTimer != null) {
            holder
                .put("on_air", new CatBoolean(true))
                .put("on_air_max_span", new CatFloat(this.onAirTimer.getMaxSpan()))
                .put("on_air_cur_span", new CatFloat(this.onAirTimer.getCurSpan()));
        }
    }

    @Override
    public void update (float delta) {
        if (this.onAirTimer != null) {
            this.onAirTimer.update(delta);
            if(this.onAirTimer.isReady()) {
                setOnGround(true);
            }
        }
        this.livingTime += delta;
        this.cycle += delta / 4;
        if (cycle > 1f) cycle %= 1f;
        // 位移由 GroundEntityCollisionSystem 统一处理（含墙体碰撞）
        this.positionOffset.set(0, MathUtils.sin(MathUtils.PI2 * this.cycle) * 0.12f);

        super.update(delta);
    }

    @Override
    public void reset () {
        setEntitySystem(null);
        setSpeed(0f);
        setPosition(0f, 0f);
        setVelocity(0f, 0f);
        setItemStack(ItemStack.VOID);
        setLivingTime(0f);
        this.cycle = 0f;    //重置悬浮动画相位
        getPositionOffset().set(0f, 0f);
        this.beingAttracted = false;    //重置吸引状态

        TaskTimer taskTimer = this.getOnAirTimer();
        if (taskTimer != null) {
            Pools.TASK_TIMER.free(taskTimer);
            this.setOnAirTimer(null);
        }
    }

    @Override
    public void freeSelf () {
        Pools.ITEM_ENTITY.free(this);
    }

    public ItemStack getItemStack () {
        return this.itemStack;
    }

    public ItemEntity setItemStack (ItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public Vector2 getPositionOffset () {
        return this.positionOffset;
    }

    public ItemEntity setPositionOffset (Vector2 positionOffset) {
        this.positionOffset = positionOffset;
        return this;
    }

    public float getLivingTime () {
        return this.livingTime;
    }

    public ItemEntity setLivingTime (float livingTime) {
        this.livingTime = livingTime;
        return this;
    }

    public TaskTimer getOnAirTimer () {
        return onAirTimer;
    }

    public ItemEntity setOnAirTimer (TaskTimer onAirTimer) {
        this.onAirTimer = onAirTimer;
        return this;
    }

    public float getCycle () {
        return this.cycle;
    }

    public ItemEntity setCycle (float cycle) {
        this.cycle = cycle;
        return this;
    }

    /**
     * 从编解码器解码出空中计时器时使用
     * <p>
     * 直接使用解码出来的计时器不便于对象池管理，因此将其数据搬到对象池中取出的计时器上
     */
    private static void setOnAirTimerFromCodec (ItemEntity entity, TaskTimer timer) {
        if (timer == null) return;
        TaskTimer pooled = Pools.TASK_TIMER.obtain()
            .setMaxSpan(timer.getMaxSpan())
            .setCurSpan(timer.getCurSpan())
            .setTask(() -> {
                Pools.TASK_TIMER.free(entity.getOnAirTimer());
                entity.setOnAirTimer(null);
            });
        entity.setOnAirTimer(pooled);
    }
}

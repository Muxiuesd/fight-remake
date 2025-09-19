package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.registry.EntityTypes;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.cat.CAT;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品实体
 * <p>
 * 掉落在地上的物品以实体形式存在
 * */
public class ItemEntity extends Entity<ItemEntity> {
    public static final Vector2 DEFAULT_SIZE = new Vector2(0.5f, 0.5f);
    private ItemStack itemStack;
    private Vector2 positionOffset;
    private TaskTimer onAirTimer;   //在空中的计时器，可以自定义物品实体在空中运动的时间
    private float cycle;
    private float livingTime;   //存在时间

    public ItemEntity (World world, EntityType<? super ItemEntity> entityType) {
        this(world);
    }
    public ItemEntity (World world) {
        super(world, EntityTypes.ITEM_ENTITY);
        this.positionOffset = new Vector2();
        setSize(DEFAULT_SIZE);
    }

    @Override
    public void readCAT (JsonValue values) {
        super.readCAT(values);
        this.cycle = values.getFloat("cycle");
        this.livingTime = values.getFloat("living_time");


        if (values.has("on_air")) {
            if (values.getBoolean("on_air")) {
                this.onAirTimer = new TaskTimer(
                    values.getFloat("on_air_max_span"),
                    values.getFloat("on_air_cur_span"),
                    () -> this.setOnAirTimer(null)
                );
            }
        }
    }

    @Override
    public void writeCAT (CAT cat) {
        super.writeCAT(cat);
        cat.set("cycle", this.cycle);
        cat.set("living_time", this.livingTime);

        if (this.onAirTimer != null) {
            cat.set("on_air", true);
            cat.set("on_air_max_span", this.onAirTimer.getMaxSpan());
            cat.set("on_air_cur_span", this.onAirTimer.getCurSpan());
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
        this.positionOffset.set(0, MathUtils.sin(MathUtils.PI2 * this.cycle) * 0.3f);

        x += delta * getCurSpeed() * velX;
        y += delta * getCurSpeed() * velY;

        super.update(delta);
    }

    @Override
    public void draw (Batch batch) {
        if (this.itemStack != null) this.itemStack.getItem().drawOnWorld(batch, this);
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

    public void setPositionOffset (Vector2 positionOffset) {
        this.positionOffset = positionOffset;
    }

    public float getLivingTime () {
        return this.livingTime;
    }

    public void setLivingTime (float livingTime) {
        this.livingTime = livingTime;
    }

    public TaskTimer getOnAirTimer () {
        return onAirTimer;
    }

    public ItemEntity setOnAirTimer (TaskTimer onAirTimer) {
        this.onAirTimer = onAirTimer;
        return this;
    }
}

package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.util.Timer;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品实体
 * <p>
 * 掉落在地上的物品以实体形式存在
 * */
public class ItemEntity extends Entity {
    public static final Vector2 DEFAULT_SIZE = new Vector2(0.7f, 0.7f);
    private ItemStack itemStack;
    private Vector2 positionOffset;
    private Timer onAirTimer;   //在空中的计时器，可以自定义物品实体在空中运动的时间
    private float cycle;
    private float livingTime;   //存在时间


    public ItemEntity () {
        initialize(Group.item);
        this.positionOffset = new Vector2();
        setSize(DEFAULT_SIZE);
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

    public void setItemStack (ItemStack itemStack) {
        this.itemStack = itemStack;
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

    public Timer getOnAirTimer () {
        return onAirTimer;
    }

    public void setOnAirTimer (Timer onAirTimer) {
        this.onAirTimer = onAirTimer;
    }
}

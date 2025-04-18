package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品实体
 * <p>
 * 掉落在地上的物品以实体形式存在
 * */
public class ItemEntity extends Entity {
    private ItemStack itemStack;
    private Vector2 positionOffset;
    private float cycle;
    private float livingTime;   //存在时间

    public ItemEntity () {
        initialize(Group.item);
        this.positionOffset = new Vector2();
    }

    @Override
    public void update (float delta) {
        this.livingTime += delta;
        this.cycle += delta / 2;
        if (cycle > 1f) cycle -= 1f;
        this.positionOffset.set(0, MathUtils.sin(MathUtils.PI2 * this.cycle) * 0.3f);
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
}

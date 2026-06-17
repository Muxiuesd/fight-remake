package ttk.muxiuesd.world.entity.abs;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import game.muxiuesd.bedrockcore.app.interfaces.serialization.Codec;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.cat.CatFloat;
import ttk.muxiuesd.world.cat.CatsHolder;
import ttk.muxiuesd.world.entity.EntityType;

/**
 * 子弹
 */
public abstract class Bullet<T extends Bullet<T>> extends Entity<T> {
    public Entity<?> owner;

    public float damage;
    private float maxLiveTime;  // 最大存活时间
    private float liveTime; // 已存活时间

    public Bullet (World world, EntityType<?> entityType) {
        super(world, entityType);
    }
    public Bullet (World world, EntityType<?> entityType, Entity<?> owner) {
        super(world, entityType);
        this.owner = owner;
    }

    public Bullet (World world, EntityType<?> entityType,
                   String textureId,
                   float damage, float speed, float maxLiveTime, float initLiveTime) {
        //先不指定所有者，等真的使用的时候再指定
        this(world, entityType, null, textureId, null, damage, speed, maxLiveTime, initLiveTime);
    }

    public Bullet (World world, EntityType<?> entityType,
                   String textureId, String texturePath,
                   float damage, float speed, float maxLiveTime, float initLiveTime) {
        //先不指定所有者，等真的使用的时候再指定
        this(world, entityType, null, textureId, texturePath, damage, speed, maxLiveTime, initLiveTime);
    }

    public Bullet (World world, EntityType<?> entityType,
                   Entity<?> owner,
                   String textureId, String texturePath,
                   float damage, float speed, float maxLiveTime, float initLiveTime) {
        super(world, entityType);
        //全部指定
        this.owner = owner;
        this.damage = damage;
        setSpeed(speed);
        this.maxLiveTime = maxLiveTime;
        this.liveTime = initLiveTime;
        //setBodyTextureRegion(getTextureRegion(textureId, texturePath));
        setBodyTextureRegionResource(textureId, texturePath);
        //默认大小
        setSize(0.5f, 0.5f);
        fastAddBodyHitBox();
    }

    @Override
    public void readCatData (JsonValue values) {
        super.readCatData(values);
        this.maxLiveTime = values.getFloat("maxLiveTime", this.maxLiveTime);
        this.liveTime = values.getFloat("liveTime", this.liveTime);
    }

    @Override
    public void writeCatData (CatsHolder holder) {
        super.writeCatData(holder);
        holder.put("maxLiveTime", new CatFloat(this.maxLiveTime));
        holder.put("liveTime", new CatFloat(this.liveTime));
    }

    @Override
    public void update (float delta) {
        this.setLiveTime(this.getLiveTime() + delta);
        //setPosition(x + getSpeed() * delta * velX, y + getSpeed() * delta * velY);
        positionChange(delta);

        super.update(delta);
    }

    public Entity<?> getOwner () {
        return this.owner;
    }

    /**
     * 设置子弹的主人，同时设置子弹所属的实体组别（与主人同组）
     * */
    public Bullet setOwner (Entity<?> owner) {
        this.owner = owner;
        return this;
    }

    public float getDamage () {
        return damage;
    }

    public Bullet setDamage (float damage) {
        this.damage = damage;
        return this;
    }

    public boolean isAlive() {
        return this.getLiveTime() <= this.getMaxLiveTime();
    }

    public float getMaxLiveTime() {
        return this.maxLiveTime;
    }

    public void setMaxLiveTime(float maxLiveTime) {
        this.maxLiveTime = maxLiveTime;
    }

    public float getLiveTime() {
        return this.liveTime;
    }

    public void setLiveTime(float liveTime) {
        this.liveTime = liveTime;
    }

    /**
     * 设置子弹的速度
     * @param direction 速度方向
     * @param speed 速度大小
     * */
    public void setVelocity (Direction direction, float speed) {
        setVelocity(direction.getX() * speed, direction.getY() * speed);
        this.setDegrees();
    }

    /**
     * 设置旋转角度，需要已知速度方向
     */
    private void setDegrees() {
        setOrigin(this.getWidth() / 2f, this.getHeight() / 2f);
        // 计算旋转角度
        Vector2 velocity = getVelocity();
        setRotation(MathUtils.atan2Deg(velocity.y, velocity.x));
    }

    @Override
    public Codec getCodec () {
        return super.getCodec();
    }
}

package ttk.muxiuesd.world.entity.abs;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.serialization.Codec;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;

/**
 * 子弹
 */
public abstract class Bullet extends Entity<Bullet> {
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
        this.speed = speed;
        this.maxLiveTime = maxLiveTime;
        this.liveTime = initLiveTime;
        bodyTexture = getTextureRegion(textureId, texturePath);
        //默认大小
        setSize(0.5f, 0.5f);
    }


    @Override
    public void update (float delta) {
        this.setLiveTime(this.getLiveTime() + delta);
        setPosition(x + getSpeed() * delta * velX, y + getSpeed() * delta * velY);
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

    public void setDirection(float xDirection, float yDirection) {
        this.velX = xDirection;
        this.velY = yDirection;
        this.setDegrees();
    }

    /**
     * 设置旋转角度，需要已知速度方向
     */
    private void setDegrees() {
        // 调整旋转原点
        setOrigin(getWidth() / 2, getHeight() / 2);
        // 计算旋转角度
        Vector2 velocity = getVelocity();
        setRotation(MathUtils.atan2Deg(velY, velX));
    }

    @Override
    public Codec getCodec () {
        return super.getCodec();
    }
}

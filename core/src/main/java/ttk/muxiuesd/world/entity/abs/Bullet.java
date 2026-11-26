package ttk.muxiuesd.world.entity.abs;


import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.serialization.Codec;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.serialization.codecs.builders.EntityCodecBuilder;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.cat.CatFloat;
import ttk.muxiuesd.world.cat.CatsHolder;
import ttk.muxiuesd.world.entity.EntityType;

/**
 * 子弹
 */
public abstract class Bullet<T extends Bullet<T>> extends Entity<T> {
    //空气阻力：每秒速度衰减到的比例（帧率无关，通过 delta 指数计算）
    private static final float AIR_DRAG_PER_SECOND = 0.98f;

    /**
     * 子弹的现代化编解码器
     * <p>
     * 解码时通过实体注册表创建实例，编码时编码基础实体与子弹的全部字段
     */
    public static final Codec<Bullet<?>> CODEC = EntityCodecBuilder.<Bullet<?>>create()
        .field("damage", Bullet::getDamage, Bullet::setDamage, Codec.FLOAT)
        .factory(EntityCodecBuilder::createEntity);

    public Entity<?> owner;

    public float damage;
    private float maxLiveTime;  // 最大存活时间
    private float liveTime; // 已存活时间
    private String textureId;   // 贴图id（数据，贴图资源实例由渲染器持有并按需加载）

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
        this.textureId = textureId;
        if (textureId != null && texturePath != null) {
            //只注册 id→路径 映射（数据），贴图资源实例由渲染器按需加载缓存
            Util.registerTextureIdPath(textureId, Fight.EntityTexturePath(texturePath));
        }
        //默认大小
        setSize(0.5f, 0.5f);
        fastAddBodyHitBox();
        setOnGround(false);
    }

    @Override
    public void readCatData (CatsHolder holder) {
        super.readCatData(holder);
        this.maxLiveTime = holder.getFloat("maxLiveTime", this.maxLiveTime);
        this.liveTime = holder.getFloat("liveTime", this.liveTime);
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

        // 位移由 BulletCollisionSystem 统一处理（含墙体碰撞与 CCD 分步）
        super.update(delta);

        // 存活时间耗尽，从实体系统移除（防止泄漏）
        if (!this.isAlive() && this.getEntitySystem() != null) {
            this.getEntitySystem().remove(this);
        }
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

    /**
     * 获取子弹的贴图id（贴图资源实例由渲染器持有）
     * */
    public String getTextureId () {
        return this.textureId;
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
}

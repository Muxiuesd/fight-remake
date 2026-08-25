package ttk.muxiuesd.world.entity.abs;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonValue;
import game.muxiuesd.bedrockcore.app.interfaces.Updateable;
import game.muxiuesd.bedrockcore.app.interfaces.audio.SpatialAudio;
import game.muxiuesd.bedrockcore.math.Vec2;
import game.muxiuesd.bedrockcore.serialization.Codec;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.data.JsonPropertiesMap;
import ttk.muxiuesd.data.abs.PropertiesDataMap;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.ICatData;
import ttk.muxiuesd.interfaces.ID;
import ttk.muxiuesd.interfaces.Tickable;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.registry.RenderLayers;
import ttk.muxiuesd.render.RenderLayer;
import ttk.muxiuesd.serialization.codecs.builders.EntityCodecBuilder;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.cat.CatBoolean;
import ttk.muxiuesd.world.cat.CatFloat;
import ttk.muxiuesd.world.cat.CatsHolder;
import ttk.muxiuesd.world.entity.EntitySounder;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.hitbox.Hitbox;
import ttk.muxiuesd.world.hitbox.HitboxHolder;
import ttk.muxiuesd.world.hitbox.RectHitbox;

/**
 * 游戏的基础实体
 * <p>
 * 拥有游戏内的坐标、运动参数；贴图资源由对应的实体渲染器持有
 */
public abstract class Entity<T extends Entity<T>>
    implements ID<T>, ICatData, Disposable, Updateable, Tickable {

    /**
     * 基础实体的现代化编解码器
     * <p>
     * 解码时通过实体注册表创建实例，编码时仅编码基础实体自身的字段
     */
    public static final Codec<Entity<?>> CODEC = EntityCodecBuilder.<Entity<?>>create()
        .factory(EntityCodecBuilder::createEntity);

    /// 实体的默认碰撞箱ID（默认就一个身体碰撞箱）
    public static final String HITBOX_BODY = Fight.ID("entity_body");

    /// 以下都是实体的基础数据（物理参数、渲染参数等）
    private float speed;
    private float x, y;                     //实体的世界坐标
    private float velX, velY;               //实体的速度
    private float frictionScale = 1f;       //当前摩擦缩放因子（由摩擦系统平滑过渡，跨方块变速不跳变）
    private float width, height;            //实体的宽高（世界渲染）
    private float originX = 0f, originY = 0f;
    private float scaleX = 1f, scaleY = 1f;
    private float rotation;                 //实体的旋转角度（世界渲染）

    private boolean onGround = true;        //实体是否接触地面，接触地面的话会受地面摩擦影响，没有的接触的话只有空气阻力

    private EntitySystem es;                        //此实体所属的实体系统
    private EntityType<?> type;                     //实体的类型
    private Property property;                      //实体的属性
    private HitboxHolder<Entity<T>> hitboxHolder;   //实体的碰撞箱持有者
    private EntitySounder sounder;                  //实体的音频发声者
    private EntityProvider<?> provider;             //实体的提供者（持有实体的注册信息与 Identifier）

    public Entity (World world, EntityType<?> type) {
        this.setType(type);
        this.property       = new Property();
        this.hitboxHolder   = new HitboxHolder<>(this);
        this.sounder        = new EntitySounder(this);
    }

    /**
     * 读取自定义属性标签数据
     * */
    @Override
    public void readCatData (JsonValue values) {
        this.speed = values.getFloat("speed", 1.145f);
        this.x = values.getFloat("x", 1.145f);
        this.y = values.getFloat("y", 1.145f);
        this.velX = values.getFloat("velX", 0);
        this.velY = values.getFloat("velY", 0);
        //curSpeed 在速度矢量之后设置（setCurSpeed 缩放已有速度矢量，顺序反了会被 MIN_SPEED 分支清零）
        this.setCurSpeed(values.getFloat("curSpeed", 1.145f));
        this.width = values.getFloat("width", 1f);
        this.height = values.getFloat("height", 1f);
        this.originX = values.getFloat("originX", 0);
        this.originY = values.getFloat("originY", 0);
        this.scaleX = values.getFloat("scaleX", 1f);
        this.scaleY = values.getFloat("scaleY", 1f);
        this.rotation = values.getFloat("rotation", 0);
        this.onGround = values.getBoolean("onGround", true);

        this.updateHitboxCenterPos(this.x, this.y);
    }

    /**
     * 写入自定义属性标签数据
     * */
    @Override
    public void writeCatData (CatsHolder holder) {
        holder
            .put("speed", new CatFloat(this.speed))
            .put("curSpeed", new CatFloat(this.getCurSpeed()))
            .put("x", new CatFloat(this.x))
            .put("y", new CatFloat(this.y))
            .put("velX", new CatFloat(this.velX))
            .put("velY", new CatFloat(this.velY))
            .put("width", new CatFloat(this.width))
            .put("height", new CatFloat(this.height))
            .put("originX", new CatFloat(this.originX))
            .put("originY", new CatFloat(this.originY))
            .put("scaleX", new CatFloat(this.scaleX))
            .put("scaleY", new CatFloat(this.scaleY))
            .put("rotation", new CatFloat(this.rotation))
            .put("onGround", new CatBoolean(this.onGround));
    }

    /**
     * 延迟初始化，在实体添加到实体系统后才会执行
     * */
    public void lazyInitialize () {}

    /**
     * 这里面调用每帧需要更新的东西，对性能影响较大，不涉及实体坐标的更新
     * */
    @Override
    public void update(float delta) {
        //更新持有的hitbox的中心点坐标
        this.updateHitboxCenterPos(this.x, this.y);
    }

    /**
     * 更新碰撞箱中心坐标，默认是跟实体坐标（默认也是中心坐标）重合的
     * */
    public void updateHitboxCenterPos (float x, float y) {
        this.getHitboxHolder().getBoxes().forEach((id, box) -> {
            box.setCenterPos(x, y);
        });
    }

    /**
     * 实体每tick调用方法
     * */
    @Override
    public void tick (World world, float delta) {
    }

    @Override
    public void dispose() {
    }

    /**
     * 快速添加一个实体身体的碰撞箱，起点和终点相对于中心的偏移值都是实体的宽高的一半
     * */
    public T fastAddBodyHitBox () {
        float halfWidth = this.getWidth() / 2f;
        float halfHeight = this.getHeight() / 2f;
        Hitbox bodyHitbox = this.getBodyHitbox();
        if (bodyHitbox == HitboxHolder.VOID_HITBOX) {
            this.addRectHitBox(HITBOX_BODY, - halfWidth, - halfHeight, halfWidth, halfHeight);
        }else if (bodyHitbox instanceof RectHitbox rectBodyHitbox) {
            rectBodyHitbox.setStartPos(- halfWidth, - halfHeight).setEndPos(halfWidth, halfHeight);
        }
        return (T) this;
    }
    /**
     * 根据起点和终点相对于中心点的偏移来添加一个矩形碰撞箱
     * */
    public T addRectHitBox (String id, float startX, float startY, float endX, float endY) {
        this.getHitboxHolder().addBox(
            id,
            new RectHitbox().setStartPos(startX, startY).setEndPos(endX, endY).setCenterPos(this.x, this.y)
        );
        return (T) this;
    }

    /**
     * 让这个实体发出某个音效
     * */
    public SpatialAudio playSound (AudioHolder sound) {
        return this.getEntitySystem()
            .getWorld()
            .getSystem(SoundSystem.class)
            .playSpatialSound(sound, this);
    }


    public T setPosition(Vector2 vector2) {
        this.setPosition(vector2.x, vector2.y);
        return (T) this;
    }
    public T setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.updateHitboxCenterPos(x, y);
        return (T) this;
    }

    public T setSize (Vector2 size) {
        this.setSize(size.x, size.y);
        return (T) this;
    }
    public T setSize(float width, float height) {
        this.width = width;
        this.height = height;
        return (T) this;
    }

    public T setBounds(float x, float y, float width, float height) {
        this.setPosition(x, y);
        this.setSize(width, height);
        return (T) this;
    }

    /**
     * 获取实体坐标
     * */
    public Vector2 getPosition() {
        return new Vector2(this.x, this.y);
    }

    /**
     * 在当前的坐标基础上做出改变
     * */
    public T positionChange (Vector2 deltaPos) {
        this.setPosition(this.x + deltaPos.x, this.y + deltaPos.y);
        return (T) this;
    }

    /**
     * 坐标根据时间间隔与速度矢量发生变化
     * @param delta 更新间隔时间
     * */
    public T positionChange (float delta) {
        this.setPosition(this.x + this.velX * delta, this.y + this.velY * delta);
        return (T) this;
    }

    /**
     * 获取实体的基准速度（比如移动速度）
     * */
    public float getSpeed () {
        return this.speed;
    }

    public T setSpeed (float speed) {
        //守卫检查新值（原来检查旧值，负速度可被写入且无法改回）
        if (speed >= 0) {
            this.speed = speed;
        }
        return (T) this;
    }

    /**
     * 获取当前速率
     * */
    public float getCurSpeed () {
        return Vec2.len(this.getVelX(), this.getVelY());
    }

    /**
     * 获取当前摩擦缩放因子（摩擦层平滑过渡用，速度 = 基准速度 × 该因子）
     * */
    public float getFrictionScale () {
        return this.frictionScale;
    }

    /**
     * 设置当前摩擦缩放因子（由摩擦系统每帧平滑更新）
     * */
    public T setFrictionScale (float frictionScale) {
        this.frictionScale = frictionScale;
        return (T) this;
    }

    /**
     * 实体是否处于击退中
     * <p>
     * 击退中的实体暂停自身意图（状态机/玩家输入），
     * 速度由击退物理与摩擦系统控制（地面受摩擦、空中受空气阻力）。
     * 默认未击退，由活物实体（{@link LivingEntity}）覆写
     * */
    public boolean isKnockback () {
        return false;
    }

    /**
     * 设置当前速率（缩放已有速度矢量到指定长度）
     * <p>
     * 注意：必须先设置速度矢量（{@link #setVelocity(float, float)}）再调用本方法；
     * 速度矢量为零时无法确定方向，保持为零
     * */
    public T setCurSpeed (float curSpeed) {
        curSpeed = Math.abs(curSpeed);
        float len = Vec2.len(this.getVelX(), this.getVelY());
        if (len < EntitySystem.MIN_SPEED) {
            //当前速度为零（没有方向可缩放），保持速度为零
            this.setVelocity(0, 0);
            return (T) this;
        }
        this.setVelocity((this.getVelX() / len) * curSpeed, (this.getVelY() / len) * curSpeed);
        return (T) this;
    }

    public float getX () {
        return this.x;
    }

    public T setX (float x) {
        this.x = x;
        //及时更新碰撞箱中心坐标
        this.updateHitboxCenterPos(this.x, this.y);
        return (T) this;
    }

    public float getY () {
        return this.y;
    }

    public T setY (float y) {
        this.y = y;
        //及时更新碰撞箱中心坐标
        this.updateHitboxCenterPos(this.x, this.y);
        return (T) this;
    }

    /**
     * 获取速度矢量
     * */
    public Vector2 getVelocity () {
        return new Vector2(this.getVelX(), this.getVelY());
    }

    /**
     * 设置速度矢量
     * */
    public T setVelocity(Vector2 velocity) {
        this.setVelocity(velocity.x, velocity.y);
        return (T) this;
    }
    public T setVelocity(float xVel, float yVel) {
        this.setVelX(xVel);
        this.setVelY(yVel);
        return (T) this;
    }

    public float getVelX () {
        return this.velX;
    }

    public T setVelX (float velX) {
        this.velX = velX;
        return (T) this;
    }

    public float getVelY () {
        return this.velY;
    }

    public T setVelY (float velY) {
        this.velY = velY;
        return (T) this;
    }

    public Vector2 getSize () {
        return new Vector2(this.width, this.height);
    }

    /**
     * 设置实体宽度（世界渲染）
     * */
    public float getWidth() {
        return this.width;
    }

    /**
     * 设置实体高度（世界渲染）
     * */
    public float getHeight() {
        return this.height;
    }

    /**
     * 设置旋转中心，这个中心是相对于贴图渲染起点的（贴图的左下角）
     * */
    public T setOrigin(float originX, float originY) {
        this.originX = originX;
        this.originY = originY;
        return (T) this;
    }

    public Vector2 getOrigin() {
        return new Vector2(this.originX, this.originY);
    }

    public Vector2 getScale () {
        return new Vector2(this.scaleX, this.scaleY);
    }

    /**
     * 获取旋转角度（世界渲染）
     * */
    public float getRotation () {
        return this.rotation;
    }

    /**
     * 设置旋转角度（世界渲染）
     * */
    public T setRotation (float rotation) {
        this.rotation = rotation;
        return (T) this;
    }

    /**
     * 获取实体的中心点的坐标（与世界坐标是两个概念），会影响实体的某些渲染坐标
     * <p>
     * 默认是实体的世界坐标
     * */
    public Vector2 getCenterPos () {
        return new Vector2(this.x, this.y);
    }

    /**
     * 获取实体的身体碰撞箱
     * */
    public Hitbox getBodyHitbox () {
        return this.getHitboxHolder().getBox(HITBOX_BODY);
    }

    /**
     * 获取实体的碰撞箱持有类
     * */
    public HitboxHolder<Entity<T>> getHitboxHolder () {
        return this.hitboxHolder;
    }

    /**
     * 设置实体碰撞箱持有类
     * */
    public T setHitboxHolder (HitboxHolder<Entity<T>> hitboxHolder) {
        if (hitboxHolder != null) this.hitboxHolder = hitboxHolder;
        return (T) this;
    }

    /**
     * 获取这个实体的发声源
     * */
    public EntitySounder getSounder () {
        return this.sounder;
    }

    public Entity<T> setSounder (EntitySounder sounder) {
        this.sounder = sounder;
        return this;
    }

    public T setEntitySystem(EntitySystem es) {
        this.es = es;
        return (T) this;
    }

    public EntitySystem getEntitySystem() {
        return this.es;
    }

    /**
     * 获取实体类型
     * */
    public EntityType<?> getType () {
        return this.type;
    }

    public T setType (EntityType<?> type) {
        this.type = type;
        return (T) this;
    }

    public Property getProperty () {
        return this.property;
    }

    public T setProperty (Property property) {
        this.property = property;
        return (T) this;
    }


    /**
     * 检查当前的状态是否是贴地的，是则有方块摩擦力，不是则无摩擦
     * */
    public boolean isOnGround () {
        return this.onGround;
    }

    /**
     * 设置当前的状态是否贴地
     * */
    public T setOnGround (boolean onGround) {
        this.onGround = onGround;
        return (T) this;
    }

    /**
     * 获取这个实体的渲染层级，默认为地面实体
     * */
    public RenderLayer getRenderLayer () {
        return RenderLayers.ENTITY_GROUND;
    }

    @Override
    public String getID () {
        return this.getIdentifier() == null ? null : this.getIdentifier().getID();
    }

    /**
     * 获取实体的标识符（由 {@link #getProvider()} 间接持有，实体自身不持有 Identifier）
     * */
    public Identifier getIdentifier () {
        return this.provider == null ? null : this.provider.getIdentifier();
    }

    /**
     * 获取实体的提供者（注册信息与 Identifier 的单一持有者）
     * */
    public EntityProvider<?> getProvider () {
        return this.provider;
    }

    /**
     * 设置实体的提供者（注册阶段由 {@link EntityProvider#create} 给定，注册过后不允许修改）
     * */
    public T setProvider (EntityProvider<?> provider) {
        if (this.provider != null && this.provider != provider) {
            throw new IllegalStateException("EntityProvider 已设置，禁止修改！实体：" + this.getID());
        }
        this.provider = provider;
        return (T) this;
    }

    /**
     * 实体的 Identifier 只能通过 {@link #setProvider} 间接设置，禁止直接设置
     * */
    @Override
    public T setIdentifier (Identifier identifier) {
        throw new IllegalStateException("实体的 Identifier 不能直接设置，应通过 EntityProvider 间接持有！实体：" + this.getID());
    }

    /**
     * 设置实体的宽度（世界渲染）
     * */
    public T setWidth (float width) {
        this.width = width;
        return (T) this;
    }

    /**
     * 设置实体的高度（世界渲染）
     * */
    public T setHeight (float height) {
        this.height = height;
        return (T) this;
    }

    /**
     * 获取旋转中心的x坐标，这个中心是相对于贴图渲染起点的（贴图的左下角）
     * */
    public float getOriginX () {
        return this.originX;
    }

    /**
     * 获取旋转中心的y坐标，这个中心是相对于贴图渲染起点的（贴图的左下角）
     * */
    public float getOriginY () {
        return this.originY;
    }

    /**
     * 设置旋转中心的x坐标，这个中心是相对于贴图渲染起点的（贴图的左下角）
     * */
    public T setOriginX (float originX) {
        this.originX = originX;
        return (T) this;
    }

    /**
     * 设置旋转中心的y坐标，这个中心是相对于贴图渲染起点的（贴图的左下角）
     * */
    public T setOriginY (float originY) {
        this.originY = originY;
        return (T) this;
    }

    /**
     * 获取x轴缩放比例
     * */
    public float getScaleX () {
        return this.scaleX;
    }

    /**
     * 获取y轴缩放比例
     * */
    public float getScaleY () {
        return this.scaleY;
    }

    /**
     * 设置x轴缩放比例
     * */
    public T setScaleX (float scaleX) {
        this.scaleX = scaleX;
        return (T) this;
    }

    /**
     * 设置y轴缩放比例
     * */
    public T setScaleY (float scaleY) {
        this.scaleY = scaleY;
        return (T) this;
    }

    /**
     * 实体的属性类
     * */
    public static class Property {
        //属性映射
        private PropertiesDataMap<?, ?, ?> propertiesMap;

        public Property () {
            setPropertiesMap(
                new JsonPropertiesMap()
                    .add(PropertyTypes.CATS, new CatsHolder())
            );
        }

        public <T> T get (PropertyType<T> propertyType) {
            return this.getPropertiesMap().get(propertyType);
        }

        public <T> Entity.Property add (PropertyType<T> propertyType, T value) {
            this.getPropertiesMap().add(propertyType, value);
            return this;
        }

        public CatsHolder getCatsHolder () {
            return this.get(PropertyTypes.CATS);
        }

        public Entity.Property setCatsHolder (CatsHolder catsHolder) {
            this.add(PropertyTypes.CATS, catsHolder);
            return this;
        }

        public PropertiesDataMap<?, ?, ?> getPropertiesMap () {
            return this.propertiesMap;
        }

        public Entity.Property setPropertiesMap (PropertiesDataMap<?, ?, ?> propertiesMap) {
            this.propertiesMap = propertiesMap;
            return this;
        }
    }
}

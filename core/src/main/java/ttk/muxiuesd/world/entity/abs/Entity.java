package ttk.muxiuesd.world.entity.abs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.data.JsonPropertiesMap;
import ttk.muxiuesd.data.abs.PropertiesDataMap;
import ttk.muxiuesd.interfaces.*;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.registry.RenderLayers;
import ttk.muxiuesd.render.RenderLayer;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.cat.CAT;
import ttk.muxiuesd.world.entity.EntityType;

/**
 * 游戏的基础实体
 * <p>
 * 拥有游戏内的坐标、运动参数以及渲染参数
 */
public abstract class Entity<T extends Entity<?>>
    implements ID<T>, ICAT, Disposable, Drawable, Updateable, ShapeRenderable, Tickable {

    private String id;

    public float speed, curSpeed;
    public float x, y;
    public float velX, velY;
    public float width, height;
    public float originX, originY;
    public float scaleX = 1, scaleY = 1;
    public float rotation;
    private boolean onGround = true;    //实体是否接触地面，接触地面的话会受地面摩擦影响，没有的接触的话只有空气阻力

    public TextureRegion bodyTexture;
    public Rectangle hitbox = new Rectangle();  //碰撞箱

    private EntitySystem es;    //此实体所属的实体系统
    private EntityType<?> type;
    private Property property;  //实体的属性

    public Entity (World world, EntityType<?> type) {
        this.setType(type);
        this.property = new Property();
    }

    @Override
    public void readCAT (JsonValue values) {
        this.speed = values.getFloat("speed", 1.145f);
        this.curSpeed = values.getFloat("curSpeed", 1.145f);
        this.x = values.getFloat("x", 1.145f);
        this.y = values.getFloat("y", 1.145f);
        this.velX = values.getFloat("velX", 0);
        this.velY = values.getFloat("velY", 0);
        this.width = values.getFloat("width", 1f);
        this.height = values.getFloat("height", 1f);
        this.originX = values.getFloat("originX", 0);
        this.originY = values.getFloat("originY", 0);
        this.scaleX = values.getFloat("scaleX", 1f);
        this.scaleY = values.getFloat("scaleY", 1f);
        this.rotation = values.getFloat("rotation", 0);
        this.onGround = values.getBoolean("onGround", true);

        //更新hitbox
        Vector2 position = getPosition();
        setCullingArea(
            position.x,
            position.y,
            getWidth(),
            getHeight()
        );
    }

    @Override
    public void writeCAT (CAT cat) {
        cat.set("speed", this.speed);
        cat.set("curSpeed", this.curSpeed);
        cat.set("x", this.x);
        cat.set("y", this.y);
        cat.set("velX", this.velX);
        cat.set("velY", this.velY);
        cat.set("width", this.width);
        cat.set("height", this.height);
        cat.set("originX", this.originX);
        cat.set("originY", this.originY);
        cat.set("scaleX", this.scaleX);
        cat.set("scaleY", this.scaleY);
        cat.set("rotation", this.rotation);
        cat.set("onGround", this.onGround);
    }

    /**
     * 延迟初始化，在实体添加到实体系统后才会执行
     * */
    public void initialize() {
    }

    @Override
    public void draw(Batch batch) {
        //最基础的绘制
        if (this.bodyTexture != null) {
            batch.draw(this.bodyTexture, this.x, this.y,
                this.originX, this.originY,
                this.width, this.height,
                this.scaleX, this.scaleY, this.rotation);
        }
    }

    @Override
    public void update(float delta) {
        this.setCullingArea(this.x, this.y, this.getWidth(), this.getHeight());
    }

    @Override
    public void tick (World world, float delta) {
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
    }

    @Override
    public void dispose() {
        if (this.bodyTexture != null) {
            this.bodyTexture = null;
        }
    }



    public T setCullingArea(float x, float y, float width, float height) {
        this.hitbox.set(x, y, width, height);
        return (T) this;
    }

    public float getSpeed () {
        return this.speed;
    }

    public T setSpeed (float speed) {
        if (this.speed >= 0) {
            this.speed = speed;
        }
        return (T) this;
    }

    public float getCurSpeed () {
        return curSpeed;
    }

    public T setCurSpeed (float curSpeed) {
        this.curSpeed = curSpeed;
        return (T) this;
    }

    public T setOrigin(float originX, float originY) {
        this.originX = originX;
        this.originY = originY;
        return (T) this;
    }

    public Vector2 getOrigin() {
        return new Vector2(this.originX, this.originY);
    }

    public T setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return (T) this;
    }

    public T setSize(float width, float height) {
        this.width = width;
        this.height = height;
        return (T) this;
    }

    public T setSize (Vector2 size) {
        this.setSize(size.x, size.y);
        return (T) this;
    }

    public T setBounds(float x, float y, float width, float height) {
        this.setPosition(x, y);
        this.setSize(width, height);
        return (T) this;
    }

    public Vector2 getPosition() {
        return new Vector2(this.x, this.y);
    }

    public T setPosition(Vector2 vector2) {
        this.x = vector2.x;
        this.y = vector2.y;
        return (T) this;
    }

    /**
     * 在当前的坐标基础上做出改变
     * */
    public T positionChange(Vector2 deltaPos) {
        this.x += deltaPos.x;
        this.y += deltaPos.y;
        return (T) this;
    }

    /**
     * 坐标根据时间间隔与速度矢量发生变化
     * @param delta 更新间隔时间
     * */
    public T positionChange(float delta) {
        this.x += this.velX * delta;
        this.y += this.velY * delta;
        return (T) this;
    }

    /**
     * 获取速度矢量
     * */
    public Vector2 getVelocity() {
        return new Vector2(this.velX, this.velY);
    }

    public T setVelocity(Vector2 velocity) {
        this.setVelocity(velocity.x, velocity.y);
        return (T) this;
    }

    public T setVelocity(float x, float y) {
        this.velX = x;
        this.velY = y;
        return (T) this;
    }

    public Vector2 getSize () {
        return new Vector2(this.width, this.height);
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public Vector2 getCenter() {
        return new Vector2(this.x + this.width / 2, this.y + this.height / 2);
    }

    public Vector2 getScale () {
        return new Vector2(this.scaleX, this.scaleY);
    }

    public float getRotation () {
        return rotation;
    }

    public T setRotation (float rotation) {
        this.rotation = rotation;
        return (T) this;
    }

    public Rectangle getHitbox() {
        return this.hitbox;
    }

    public T setHitbox (Rectangle hitbox) {
        this.hitbox = hitbox;
        return (T) this;
    }

    public T setEntitySystem(EntitySystem es) {
        this.es = es;
        return (T) this;
    }

    public EntitySystem getEntitySystem() {
        return this.es;
    }

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
     * 加载身体材质
     * */
    public void loadBodyTextureRegion (String textureId, String texturePath) {
        bodyTexture = this.getTextureRegion(textureId, texturePath);
    }

    /**
     * 加载纹理区域
     * @param textureId 纹理材质id
     * @param texturePath 实体纹理材质在 texture/entity 下的路径，当此为null时则默认之前手动加载过
     * */
    public TextureRegion getTextureRegion (String textureId, String texturePath) {
        if (texturePath == null) {
            return new TextureRegion(AssetsLoader.getInstance().getById(textureId, Texture.class));
        }

        AssetsLoader.getInstance().loadAsync(textureId, Fight.EntityTexturePath(texturePath), Texture.class, null);
        Texture texture = AssetsLoader.getInstance().getById(textureId, Texture.class);
        return new TextureRegion(texture);
    }

    /**
     * 检查当前的状态是否是贴地的，是则有方块摩擦力，不是则无摩擦
     * */
    public boolean isOnGround () {
        return onGround;
    }

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
        return this.id;
    }
    @Override
    public T setID (String id) {
        this.id = id;
        return (T) this;
    }

    /**
     * 实体的属性
     * */
    public static class Property {
        //属性映射
        private PropertiesDataMap<?, ?, ?> propertiesMap;

        public Property () {
            setPropertiesMap(
                new JsonPropertiesMap()
                    .add(PropertyTypes.CAT, new CAT())
            );
        }

        public <T> T get (PropertyType<T> propertyType) {
            return getPropertiesMap().get(propertyType);
        }

        public <T> Entity.Property add (PropertyType<T> propertyType, T value) {
            getPropertiesMap().add(propertyType, value);
            return this;
        }

        public CAT getCAT () {
            return this.get(PropertyTypes.CAT);
        }

        public Entity.Property setCAT (CAT cat) {
            return this.add(PropertyTypes.CAT, cat);
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

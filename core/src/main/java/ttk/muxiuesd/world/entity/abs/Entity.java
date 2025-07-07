package ttk.muxiuesd.world.entity.abs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.ID;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.system.GroundEntitySystem;
import ttk.muxiuesd.world.entity.Group;

/**
 * 游戏的基础实体
 * <p>
 * 拥有游戏内的坐标、运动参数以及渲染参数
 */
public abstract class Entity implements ID<Entity>, Disposable, Drawable, Updateable, ShapeRenderable {
    private String id;

    public Group group;
    public float speed, curSpeed;
    public float x, y;
    public float velX, velY;
    public float width, height;
    public float originX, originY;
    public float scaleX = 1, scaleY = 1;
    public float rotation;
    public TextureRegion bodyTexture;
    public Rectangle hitbox = new Rectangle();  //碰撞箱

    private boolean onGround = true;    //实体是否接触地面，接触地面的话会受地面摩擦影响，没有的接触的话只有空气阻力
    private GroundEntitySystem es;    //此实体所属的实体系统

    /**
     * 延迟初始化
     * */
    public void initialize(Group group) {
        this.group = group;
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
    public void renderShape (ShapeRenderer batch) {
    }

    @Override
    public void dispose() {
        if (this.bodyTexture != null) {
            this.bodyTexture = null;
        }
    }

    public Entity setCullingArea(float x, float y, float width, float height) {
        this.hitbox.set(x, y, width, height);
        return this;
    }

    public float getSpeed () {
        return this.speed;
    }

    public Entity setSpeed (float speed) {
        if (this.speed >= 0) {
            this.speed = speed;
        }
        return this;
    }

    public float getCurSpeed () {
        return curSpeed;
    }

    public Entity setCurSpeed (float curSpeed) {
        this.curSpeed = curSpeed;
        return this;
    }

    public Entity setOrigin(float originX, float originY) {
        this.originX = originX;
        this.originY = originY;
        return this;
    }

    public Vector2 getOrigin() {
        return new Vector2(this.originX, this.originY);
    }

    public Entity setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Entity setSize(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public Entity setSize (Vector2 size) {
        this.setSize(size.x, size.y);
        return this;
    }

    public Entity setBounds(float x, float y, float width, float height) {
        this.setPosition(x, y);
        this.setSize(width, height);
        return this;
    }

    public Vector2 getPosition() {
        return new Vector2(this.x, this.y);
    }

    public Entity setPosition(Vector2 vector2) {
        this.x = vector2.x;
        this.y = vector2.y;
        return this;
    }

    public Vector2 getVelocity() {
        return new Vector2(this.velX, this.velY);
    }

    public Entity setVelocity(Vector2 velocity) {
        this.setVelocity(velocity.x, velocity.y);
        return this;
    }

    public Entity setVelocity(float x, float y) {
        this.velX = x;
        this.velY = y;
        return this;
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

    public Entity setRotation (float rotation) {
        this.rotation = rotation;
        return this;
    }

    public Entity setEntitySystem(GroundEntitySystem es) {
        this.es = es;
        return this;
    }

    public GroundEntitySystem getEntitySystem() {
        return this.es;
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

    public boolean isOnGround () {
        return onGround;
    }

    public void setOnGround (boolean onGround) {
        this.onGround = onGround;
    }

    @Override
    public String getID () {
        return this.id;
    }
    @Override
    public Entity setID (String id) {
        this.id = id;
        return this;
    }
}

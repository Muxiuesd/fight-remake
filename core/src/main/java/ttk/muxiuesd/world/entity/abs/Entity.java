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
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.world.entity.Group;

/**
 * 基础实体
 */
public abstract class Entity implements ID, Disposable, Drawable, Updateable, ShapeRenderable {
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
    public Rectangle hurtbox = new Rectangle();

    public boolean attacked = false;
    private boolean onGround = true;
    private EntitySystem es;    //此实体所属的实体系统

    /**
     * 延迟初始化
     * */
    public void initialize(Group group) {
        this.group = group;
    }

    public void draw(Batch batch) {
        if (this.attacked) {
            // 受到攻击变红
            batch.setColor(255, 0, 0, 255);
        }
        if (bodyTexture != null) {
            batch.draw(bodyTexture, x, y,
                originX, originY,
                width, height,
                scaleX, scaleY, rotation);
        }
        // 还原batch
        batch.setColor(255, 255, 255, 255);
        attacked = false;
    }

    public void update(float delta) {
        this.setCullingArea(x, y, this.getWidth(), this.getHeight());
    }

    @Override
    public void renderShape (ShapeRenderer batch) {

    }

    public void setCullingArea(float x, float y, float width, float height) {
        this.hurtbox.set(x, y, width, height);
    }

    public void dispose() {
        if (this.bodyTexture != null) {
            this.bodyTexture = null;
        }
    }

    public float getSpeed () {
        return speed;
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

    public void setOrigin(float originX, float originY) {
        this.originX = originX;
        this.originY = originY;
    }

    /**
     * Sets the position of the actor's bottom left corner.
     */
    public void setPosition(float x, float y) {
        if (this.x != x || this.y != y) {
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Sets the width and height.
     */
    public void setSize(float width, float height) {
        if (this.width != width || this.height != height) {
            this.width = width;
            this.height = height;
        }
    }

    public void setSize (Vector2 size) {
        this.setSize(size.x, size.y);
    }

    /**
     * Set bounds the x, y, width, and height.
     */
    public void setBounds(float x, float y, float width, float height) {
        setPosition(x, y);
        setSize(width, height);
    }

    public Vector2 getPosition() {
        return new Vector2(this.x, this.y);
    }

    public void setPosition(Vector2 vector2) {
        this.x = vector2.x;
        this.y = vector2.y;
    }

    public Vector2 getVelocity() {
        return new Vector2(this.velX, this.velY);
    }

    public void setVelocity(Vector2 velocity) {
        this.setVelocity(velocity.x, velocity.y);
    }

    public void setVelocity(float x, float y) {
        this.velX = x;
        this.velY = y;
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

    public Vector2 getOrigin() {
        return new Vector2(this.originX, this.originY);
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

    public void setEntitySystem(EntitySystem es) {
        this.es = es;
    }

    public EntitySystem getEntitySystem() {
        return this.es;
    }

    @Override
    public String getID () {
        return this.id;
    }
    @Override
    public void setID (String id) {
        this.id = id;
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
}

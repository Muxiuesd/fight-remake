package ttk.muxiuesd.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.ID;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.system.EntitySystem;

/**
 * 基础实体
 */
public abstract class Entity implements ID, Disposable, Drawable, Updateable {
    private String id;

    public Group group;
    public float speed, curSpeed;
    public float x, y;
    public float velX, velY;
    public float width, height;
    public float originX, originY;
    public float scaleX = 1, scaleY = 1;
    public float rotation;
    public TextureRegion textureRegion;
    public Rectangle hurtbox = new Rectangle();

    public boolean attacked = false;

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
        if (textureRegion != null) {
            batch.draw(textureRegion, x, y,
                originX, originY,
                width, height,
                scaleX, scaleY, rotation);
        }
        // 还原batch
        batch.setColor(255, 255, 255, 255);
        attacked = false;
    }

    public void update(float delta) {
        this.setCullingArea(x, y, width, height);
    }

    public void setCullingArea(float x, float y, float width, float height) {
        this.hurtbox.set(x, y, width, height);
    }

    public void dispose() {
        if (this.textureRegion != null) {
            this.textureRegion = null;
        }
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

    public Vector2 getVelocity() {
        return new Vector2(this.velX, this.velY);
    }

    public void setPosition(Vector2 vector2) {
        this.x = vector2.x;
        this.y = vector2.y;
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
}

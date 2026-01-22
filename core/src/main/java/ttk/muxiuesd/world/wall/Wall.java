package ttk.muxiuesd.world.wall;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.hitbox.Hitbox;
import ttk.muxiuesd.world.hitbox.HitboxHolder;
import ttk.muxiuesd.world.hitbox.RectHitbox;

/**
 * 墙体，即有碰撞箱的特殊方块，目前也是一个墙体一个实例
 * */
public abstract class Wall<T extends Wall<T>> extends Block implements ShapeRenderable {
    public static final String DEFAULT_HITBOX_ID = Fight.ID("wall");

    public float x, y;
    private HitboxHolder<T> hitboxHolder;


    private Rectangle hitbox;

    public Wall(Property property, String textureId, String texturePath) {
        super(property, textureId, texturePath);
        this.hitboxHolder = new HitboxHolder<>();
        this.hitboxHolder.addBox(DEFAULT_HITBOX_ID, new RectHitbox());
    }

    /**
     * 创建一个墙体实例
     * */
    public abstract T createSelf (Vector2 position);

    @Override
    public void renderShape(ShapeRenderer batch) {
        if (this.getHitbox() != null) {
            batch.rect(hitbox.x, hitbox.y, hitbox.width, hitbox.height);
        }
    }

    public Rectangle getHitbox() {
        return this.hitbox;
    }

    /**
     * 设置默认的hitbox，以墙的坐标来设置
     * */
    public Wall<T> setHitbox() {
        return this.setHitbox(x, y);
    }

    public Wall<T> setHitbox(float startX, float startY) {

        Hitbox box = this.getHitboxHolder().getBox(DEFAULT_HITBOX_ID);
        box.setCenterPos(startX, startY);


        return this.setHitbox(new Rectangle().set(startX, startY, BlockWidth, BlockHeight));
    }

    public Wall<T> setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
        return this;
    }

    public void setPosition (Vector2 position) {
        this.setPosition(position.x, position.y);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.setHitbox();
    }

    public HitboxHolder<T> getHitboxHolder () {
        return this.hitboxHolder;
    }

    public Wall<T> setHitboxHolder (HitboxHolder<T> hitboxHolder) {
        if (hitboxHolder != null) this.hitboxHolder = hitboxHolder;
        return this;
    }
}

package ttk.muxiuesd.world.wall;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 墙体，即有碰撞箱的特殊方块，目前也是一个墙体一个实例
 * */
public abstract class Wall<T extends Wall<T>> extends Block implements ShapeRenderable {
    public float x, y;
    private Rectangle hitbox;

    public Wall(Property property, String textureId, String texturePath) {
        super(property, textureId, texturePath);
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
}

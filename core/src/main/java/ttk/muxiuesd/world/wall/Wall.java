package ttk.muxiuesd.world.wall;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 墙体，即有碰撞箱的特殊方块
 * */
public abstract class Wall extends Block implements ShapeRenderable {
    private Rectangle hitbox;

    public Wall(Property property, String textureId, String texturePath) {
        super(property, textureId, texturePath);
    }

    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        this.setHitbox();
    }

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
    public Wall setHitbox() {
        return this.setHitbox(x, y);
    }

    public Wall setHitbox(float startX, float startY) {
        return this.setHitbox(new Rectangle().set(startX, startY, BlockWidth, BlockHeight));
    }

    public Wall setHitbox(Rectangle hitbox) {
        this.hitbox = hitbox;
        return this;
    }
}

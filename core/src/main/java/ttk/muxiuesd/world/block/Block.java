package ttk.muxiuesd.world.block;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.interfaces.BlockDrawable;
import ttk.muxiuesd.interfaces.Updateable;

/**
 * 方块
 * */
public abstract class Block implements Updateable, BlockDrawable, Disposable {
    public static final float BlockWidth = 1f, BlockHeight = 1f;

    public TextureRegion textureRegion;

    public float x, y;
    public float width = BlockWidth, height = BlockHeight;
    public float originX = 0, originY = 0;
    public float scaleX = 1, scaleY = 1;
    public float rotation = 0;

    private Property property;

    public Block(Property property, String texturePath) {
        this.setProperty(property);
        this.loadTextureRegion(texturePath);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void draw(Batch batch) {
        if (this.textureIsValid()) {
            batch.draw(this.textureRegion,
                this.x, this.y,
                this.originX, this.originY,
                this.width, this.height,
                this.scaleX, this.scaleY,
                this.rotation);
        }
    }

    public void setPosition (float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void dispose() {
        if (this.textureRegion != null) {
            this.textureRegion = null;
        }
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public void loadTextureRegion(String texturePath) {
        AssetsLoader.getInstance().loadAsync(texturePath, Texture.class, () -> {
            Texture texture = AssetsLoader.getInstance().get(texturePath, Texture.class);
            this.textureRegion = new TextureRegion(texture);
        });
    }

    public boolean textureIsValid() {
        return this.textureRegion != null;
    }

    /**方块属性
     * 使用构建者模式
     * */
    public static class Property {
        private float friction;

        public Property() {
        }

        public float getFriction() {
            return friction;
        }

        public Property setFriction(float friction) {
            this.friction = friction;
            return this;
        }
    }
}

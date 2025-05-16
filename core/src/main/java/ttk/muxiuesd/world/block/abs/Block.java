package ttk.muxiuesd.world.block.abs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.interfaces.BlockDrawable;
import ttk.muxiuesd.interfaces.ID;
import ttk.muxiuesd.world.block.BlockSoundsID;

/**
 * 方块
 * */
public abstract class Block implements ID, BlockDrawable, Disposable {
    public static final float BlockWidth = 1f, BlockHeight = 1f;

    private String id;
    public TextureRegion textureRegion;

    public float width = BlockWidth, height = BlockHeight;
    public float originX = 0, originY = 0;
    public float scaleX = 1, scaleY = 1;
    public float rotation = 0;

    private Property property;

    public Block (Property property, String textureId) {
        this.setProperty(property);
        this.textureRegion = this.loadTextureRegion(textureId);
    }

    public Block(Property property, String textureId, String texturePath) {
        this.setProperty(property);
        this.textureRegion = this.loadTextureRegion(textureId, texturePath);
    }

    @Override
    public void draw(Batch batch, float x, float y) {
        if (this.textureIsValid()) {
            batch.draw(this.textureRegion,
                x, y,
                this.originX, this.originY,
                this.width, this.height,
                this.scaleX, this.scaleY,
                this.rotation);
        }
    }

    @Override
    public void dispose() {
        if (this.textureRegion != null) {
            this.textureRegion = null;
        }
    }

    public Property getProperty () {
        return property;
    }

    public void setProperty (Property property) {
        this.property = property;
    }

    public TextureRegion loadTextureRegion (String id) {
        return this.loadTextureRegion(id, null);
    }

    /**
     * 获取材质
     * <p>
     * 有返回值，以便于有多个材质的物品使用
     * @param texturePath 当此为null时默认之前加载过
     * */
    public TextureRegion loadTextureRegion (String id, String texturePath) {
        if (texturePath == null) {
            texturePath = AssetsLoader.getInstance().getPath(id);
        }

        AssetsLoader.getInstance().loadAsync(id, texturePath, Texture.class, null);
        return new TextureRegion(AssetsLoader.getInstance().getById(id, Texture.class));
    }

    public boolean textureIsValid() {
        return this.textureRegion != null;
    }

    @Override
    public String getID () {
        return this.id;
    }
    @Override
    public void setID (String id) {
        this.id = id;
    }

    /**方块属性
     * 使用构建者模式
     * */
    public static class Property {
        private float friction;
        private BlockSoundsID sounds;

        public Property() {
        }

        public float getFriction() {
            return friction;
        }

        public Property setFriction(float friction) {
            this.friction = friction;
            return this;
        }

        public BlockSoundsID getSounds() {
            if (this.sounds == null) {
                //默认音效
                this.sounds = BlockSoundsID.DEFAULT;
            }
            return this.sounds;
        }

        public Property setSounds(BlockSoundsID sounds) {
            this.sounds = sounds;
            return this;
        }
    }
}

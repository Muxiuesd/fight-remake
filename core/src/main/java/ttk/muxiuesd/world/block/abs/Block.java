package ttk.muxiuesd.world.block.abs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.interfaces.BlockDrawable;
import ttk.muxiuesd.interfaces.ID;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.world.block.BlockSoundsID;

import java.util.Objects;

/**
 * 方块
 * */
public abstract class Block implements ID, Updateable, BlockDrawable, Disposable {
    public static final float BlockWidth = 1f, BlockHeight = 1f;

    private String id;
    public TextureRegion textureRegion;

    public float x, y;
    public float width = BlockWidth, height = BlockHeight;
    public float originX = 0, originY = 0;
    public float scaleX = 1, scaleY = 1;
    public float rotation = 0;

    private Property property;

    public Block (Property property, String textureId) {
        this.setProperty(property);
        this.loadTextureRegion(textureId);
    }

    public Block(Property property, String textureId, String texturePath) {
        this.setProperty(property);
        this.loadTextureRegion(textureId, texturePath);
    }

    @Override
    public void update(float delta) {

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

    public Property getProperty () {
        return property;
    }

    public void setProperty (Property property) {
        this.property = property;
    }

    public void loadTextureRegion (String id) {
        this.textureRegion = new TextureRegion(AssetsLoader.getInstance().getById(id, Texture.class));
    }

    public void loadTextureRegion (String id, String texturePath) {
        AssetsLoader.getInstance().loadAsync(id, texturePath, Texture.class, () -> {
            Texture texture = AssetsLoader.getInstance().getById(id, Texture.class);
            this.textureRegion = new TextureRegion(texture);
        });
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

        private String walkSoundId;
        private String putSoundId;
        private String destroySoundId;

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

        public String getWalkSoundId () {
            //为null则返回默认情况
            return Objects.requireNonNullElse(walkSoundId, Fight.getId("grass_walk"));
        }

        public Property setWalkSoundId (String walkSoundId) {
            this.walkSoundId = walkSoundId;
            return this;
        }
    }
}

package ttk.muxiuesd.world.block.abs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.data.JsonPropertiesMap;
import ttk.muxiuesd.data.abs.PropertiesDataMap;
import ttk.muxiuesd.interfaces.ICAT;
import ttk.muxiuesd.interfaces.ID;
import ttk.muxiuesd.interfaces.world.block.BlockDrawable;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.world.block.BlockSoundsID;
import ttk.muxiuesd.world.cat.CAT;

/**
 * 方块
 * */
public abstract class Block implements ID<Block>, BlockDrawable, Disposable, ICAT {
    private static final PropertiesDataMap<JsonPropertiesMap> BLOCK_DEFAULT_PROPERTIES_DATA_MAP = new JsonPropertiesMap()
        .add(PropertyTypes.CAT, new CAT())
        .add(PropertyTypes.BLOCK_FRICTON, 1f)
        .add(PropertyTypes.BLOCK_SOUNDS_ID, BlockSoundsID.DEFAULT);
    //public static final Property DEFAULT_PROPERTY = new Property();

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
    public Block setID (String id) {
        this.id = id;
        return this;
    }

    @Override
    public void writeCAT (CAT cat) {
        cat.set("id", this.getID());
        cat.set("width", this.width);
        cat.set("height", this.height);
        cat.set("originX", this.originX);
        cat.set("originY", this.originY);
        cat.set("scaleX", this.scaleX);
        cat.set("scaleY", this.scaleY);
        cat.set("rotation", this.rotation);
    }

    @Override
    public void readCAT (JsonValue values) {
        this.setID(values.getString("id"));
        this.width = values.getFloat("width");
        this.height = values.getFloat("height");
        this.originX = values.getFloat("originX");
        this.originY = values.getFloat("originY");
        this.scaleX = values.getFloat("scaleX");
        this.scaleY = values.getFloat("scaleY");
        this.rotation = values.getFloat("rotation");
    }

    /**方块属性
     * 使用构建者模式
     * */
    public static class Property {


        private PropertiesDataMap<?> propertiesDataMap;


        public Property() {
            this.propertiesDataMap = BLOCK_DEFAULT_PROPERTIES_DATA_MAP.copy();
        }

        public CAT getCAT () {
            return this.propertiesDataMap.get(PropertyTypes.CAT);
        }

        public void setCAT (CAT cat) {
            this.propertiesDataMap.add(PropertyTypes.CAT, cat);
        }

        public float getFriction() {
            return this.propertiesDataMap.get(PropertyTypes.BLOCK_FRICTON);
        }

        public Property setFriction(float friction) {
            if (friction >= 0f) this.propertiesDataMap.add(PropertyTypes.BLOCK_FRICTON, friction);
            return this;
        }

        public BlockSoundsID getSounds() {
            return this.propertiesDataMap.get(PropertyTypes.BLOCK_SOUNDS_ID);
        }

        public Property setSounds(BlockSoundsID sounds) {
            this.propertiesDataMap.add(PropertyTypes.BLOCK_SOUNDS_ID, sounds);
            return this;
        }

        public PropertiesDataMap<?> getPropertiesMap () {
            return propertiesDataMap;
        }

        public Property setPropertiesMap (PropertiesDataMap<?> propertiesDataMap) {
            this.propertiesDataMap = propertiesDataMap;
            return this;
        }
    }
}

package ttk.muxiuesd.world.block.abs;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.data.JsonPropertiesMap;
import ttk.muxiuesd.data.abs.PropertiesDataMap;
import ttk.muxiuesd.interfaces.ICAT;
import ttk.muxiuesd.interfaces.ID;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.block.BlockSoundsID;
import ttk.muxiuesd.world.cat.CAT;

/**
 * 方块
 * */
public abstract class Block implements ID<Block>, Disposable, ICAT {
    private static final JsonPropertiesMap BLOCK_DEFAULT_PROPERTIES_DATA_MAP = new JsonPropertiesMap()
        .add(PropertyTypes.BLOCK_FRICTON, 1f)
        .add(PropertyTypes.BLOCK_SOUNDS_ID, Sounds.STONE);

    public static final float BlockWidth = 1f, BlockHeight = 1f;

    /**
     * 生成默认的属性
     * 有些需要实例化的东西就放里面防止浅拷贝
     * */
    public static Property createProperty() {
        return new Property().setCAT(new CAT());
    }

    private String id;
    public TextureRegion textureRegion;

    private Property property;

    public Block(Property property) {
        this.setProperty(property);
    }
    public Block (Property property, String textureId) {
        this(property, textureId, null);
    }
    public Block(Property property, String textureId, String texturePath) {
        this.setProperty(property);
        this.textureRegion = Util.loadTextureRegion(textureId, texturePath);
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

    public boolean textureIsValid() {
        return this.textureRegion != null;
    }

    public TextureRegion getTextureRegion () {
        return textureRegion;
    }

    public Block setTextureRegion (TextureRegion textureRegion) {
        this.textureRegion = textureRegion;
        return this;
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

    /**
     * 在方块属性写入前一刻调用
     * */
    @Override
    public void writeCAT (CAT cat) {
    }

    /**
     * 从json中获取值
     * */
    @Override
    public void readCAT (JsonValue values) {
    }

    /**方块属性
     * 使用构建者模式
     * */
    public static class Property {
        private PropertiesDataMap<?, ?, ?> propertiesDataMap;

        private Property() {
            ////这里有可能浅拷贝
            this.propertiesDataMap = BLOCK_DEFAULT_PROPERTIES_DATA_MAP.copy();
        }

        public CAT getCAT () {
            return this.propertiesDataMap.get(PropertyTypes.CAT);
        }

        public Property setCAT (CAT cat) {
            return this.set(PropertyTypes.CAT, cat);
        }

        public float getFriction() {
            return this.get(PropertyTypes.BLOCK_FRICTON);
        }

        public Property setFriction(float friction) {
            if (friction >= 0f) return this.set(PropertyTypes.BLOCK_FRICTON, friction);
            return this;
        }

        public BlockSoundsID getSounds() {
            return this.get(PropertyTypes.BLOCK_SOUNDS_ID);
        }

        public Property setSounds(BlockSoundsID sounds) {
            return this.set(PropertyTypes.BLOCK_SOUNDS_ID, sounds);
        }

        /**
         * 设置属性
         * */
        public <T> Property set (PropertyType<T> property, T value) {
            this.getPropertiesMap().add(property, value);
            return this;
        }

        /**
         * 获取属性
         * */
        public <T> T get (PropertyType<T> property) {
            return this.getPropertiesMap().get(property);
        }

        public PropertiesDataMap<?, ?, ?> getPropertiesMap () {
            return this.propertiesDataMap;
        }

        public Property setPropertiesMap (PropertiesDataMap<?, ?, ?> propertiesDataMap) {
            this.propertiesDataMap = propertiesDataMap;
            return this;
        }
    }
}

package ttk.muxiuesd.world.block.abs;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import ttk.muxiuesd.data.JsonPropertiesMap;
import ttk.muxiuesd.data.abs.PropertiesDataMap;
import ttk.muxiuesd.interfaces.ID;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.block.BlockSoundsID;
import ttk.muxiuesd.world.cat.CatsHolder;

/**
 * 方块
 * */
public abstract class Block implements ID<Block>, Disposable {
    /// 方块的大小（最基础的属性）
    public static final float WIDTH = 1f, HEIGHT = 1f;
    /// 方块碰撞箱坐标偏移，自带正负号
    public static final float HITBOX_START_X_OFFSET = - WIDTH / 2, HITBOX_START_Y_OFFSET = - HEIGHT / 2;
    public static final float HITBOX_END_X_OFFSET = WIDTH / 2, HITBOX_END_Y_OFFSET = HEIGHT / 2;

    /**
     * 生成默认的属性
     * 有些需要实例化的东西就放里面防止浅拷贝
     * */
    public static Property createProperty() {
        return new Property();
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
        return this.property;
    }

    public void setProperty (Property property) {
        this.property = property;
    }

    /**
     * 检测方快的材质贴图是否存在
     * */
    public boolean textureIsValid() {
        return this.getTextureRegion() != null;
    }

    public TextureRegion getTextureRegion () {
        return this.textureRegion;
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

    /**方块属性
     * 使用构建者模式
     * */
    public static class Property {
        private static final JsonPropertiesMap BLOCK_DEFAULT_PROPERTIES_DATA_MAP = new JsonPropertiesMap()
            .add(PropertyTypes.BLOCK_FRICTON, 1f)
            .add(PropertyTypes.BLOCK_SOUNDS_ID, Sounds.STONE);

        private PropertiesDataMap<?, ?, ?> propertiesDataMap;

        public Property() {
            /// 这里有可能浅拷贝
            this.propertiesDataMap = new JsonPropertiesMap()
                .add(PropertyTypes.BLOCK_FRICTON, 1f)
                .add(PropertyTypes.BLOCK_SOUNDS_ID, Sounds.STONE)
                .add(PropertyTypes.CATS, new CatsHolder());
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

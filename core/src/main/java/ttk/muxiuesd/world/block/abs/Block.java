package ttk.muxiuesd.world.block.abs;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.CodecBuilder;
import ttk.muxiuesd.data.JsonPropertiesMap;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.ID;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.resource.Resource;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.BlockSounds;
import ttk.muxiuesd.world.cat.CatsHolder;

/**
 * 方块
 * <p>
 * 普通方块的都是全世界一个实例对象，在不同的坐标上多次渲染（享元模式）
 * */
public class Block implements ID<Block>, Disposable {
    /**
     * 普通方块的编解码器
     * */
    public static final Codec<Block> CODEC = Codec.STRING.xmap(
        Registries.BLOCK::get,
        Block::getID
    );


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
        return Property.create();
    }

    private Identifier identifier;
    private Resource<TextureRegion> textureResource;
    private Property property;


    public Block (Property property) {
        this.setProperty(property);
    }
    public Block (Property property, String textureId) {
        this(property, textureId, null);
    }
    public Block (Property property, String textureId, String texturePath) {
        this.setProperty(property);
        //this.textureRegion = Util.loadTextureRegion(textureId, texturePath);
        this.textureResource = Resource.ofTextureRegion(textureId, texturePath);
    }

    /**
     * 方块被破坏调用的方法
     * */
    public void beDestroyed (World world, Vector2 position) {
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
        return this.textureResource.get();
    }

    @Override
    public String getID () {
        return this.getIdentifier().getID();
    }
    @Override
    public Block setID (String id) {
        this.getIdentifier().setID(id);
        return this;
    }

    public Identifier getIdentifier () {
        return this.identifier;
    }

    public Block setIdentifier (Identifier identifier) {
        this.identifier = identifier;
        return this;
    }

    @Override
    public void dispose() {
    }


    /**
     * 方块的属性类
     * */
    public static class Property {
        public static final Codec<Block.Property> CODEC = CodecBuilder.create(Block.Property::new)
            .field("data_map", Block.Property::getPropertiesMap, Block.Property::setPropertiesMap, JsonPropertiesMap.CODEC)
            .build();

        public static Property create () {
            return new Property();
        }

        private JsonPropertiesMap propertiesDataMap;

        public Property () {
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

        public BlockSounds getSounds() {
            return this.get(PropertyTypes.BLOCK_SOUNDS_ID);
        }

        public Property setSounds(BlockSounds sounds) {
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

        public JsonPropertiesMap getPropertiesMap () {
            return this.propertiesDataMap;
        }

        public Property setPropertiesMap (JsonPropertiesMap propertiesDataMap) {
            this.propertiesDataMap = propertiesDataMap;
            return this;
        }
    }
}

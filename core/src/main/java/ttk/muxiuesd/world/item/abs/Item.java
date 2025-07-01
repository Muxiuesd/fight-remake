package ttk.muxiuesd.world.item.abs;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.data.JsonPropertiesMap;
import ttk.muxiuesd.data.abs.PropertiesDataMap;
import ttk.muxiuesd.interfaces.ID;
import ttk.muxiuesd.interfaces.world.item.ItemRenderable;
import ttk.muxiuesd.interfaces.world.item.ItemShapeRenderable;
import ttk.muxiuesd.interfaces.world.item.ItemUpdateable;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品的模板类
 * <p>
 * 游戏中一种物品只有一个实例，同一种物品的不同物品堆叠都持有同一个物品实例，对这个物品实例的修改会影响整个游戏的相同物品
 * */
public abstract class Item implements ID<Item>, ItemUpdateable, ItemRenderable, ItemShapeRenderable {
    public static final PropertiesDataMap<?> ITEM_DEFAULT_PROPERTIES_DATA_MAP = new JsonPropertiesMap()
        .add(PropertyTypes.ITEM_MAX_COUNT, 64)
        .add(PropertyTypes.ITEM_ON_USING, false)
        .add(PropertyTypes.ITEM_USE_SOUND_ID, Sounds.ITEM_CLICK.getId());

    private String id;
    public Type type;
    public Property property;
    public TextureRegion texture;

    public Item (Type type, Property property, String textureId) {
        this(type, property, textureId, null);
    }
    public Item (Type type, Property property, String textureId, String texturePath) {
        this.type = type;
        this.property = property;
        this.loadTextureRegion(textureId, texturePath);
    }

    /**
     * 在持有者手上持有时的绘制方法
     * TODO 不同类型的物品不同的绘制方式
     * */
    @Override
    public void drawOnHand (Batch batch, LivingEntity holder, ItemStack itemStack) {
        /*if (this.texture != null) {
            Direction direction = Util.getDirection();
            float rotation = MathUtils.atan2Deg360(direction.getyDirection(), direction.getxDirection()) - 45;
            batch.draw(this.texture, holder.x + holder.getWidth() / 2, holder.y + holder.getHeight() / 2,
                0, 0,
                holder.width, holder.height,
                holder.scaleX, holder.scaleY, rotation);
        }*/

        Direction direction = holder.getDirection();
        float rotation = MathUtils.atan2Deg360(direction.getyDirection(), direction.getxDirection());
        if (rotation > 90f && rotation <= 270f) {
            batch.draw(this.texture, holder.x + holder.getWidth() / 2, holder.y + holder.getHeight() / 2,
                0, 0,
                holder.width, holder.height,
                - holder.scaleX, holder.scaleY, rotation + 225f);
        } else {
            batch.draw(this.texture, holder.x + holder.getWidth() / 2, holder.y + holder.getHeight() / 2,
                0, 0,
                holder.width, holder.height,
                holder.scaleX, holder.scaleY, rotation - 45f);
        }
    }

    /**
     * 在掉落物形式下的绘制方法
     * @param itemEntity 所属的物品实体
     * */
    @Override
    public void drawOnWorld (Batch batch, ItemEntity itemEntity) {
        if (this.texture != null) {
            batch.draw(this.texture, itemEntity.x, itemEntity.y + itemEntity.getPositionOffset().y,
                itemEntity.originX, itemEntity.originY,
                itemEntity.width, itemEntity.height,
                itemEntity.scaleX, itemEntity.scaleY, itemEntity.rotation);
        }
    }

    @Override
    public void update (float delta, ItemStack itemStack) {
    }

    @Override
    public void renderShape (ShapeRenderer batch, ItemStack itemStack) {
    }

    /**
     * 使用此物品
     * @return 是否使用成功
     * */
    public boolean use (ItemStack itemStack, World world, LivingEntity user) {
        //播放物品使用音效
        String useSoundId = this.property.getUseSoundId();
        SoundEffectSystem ses = (SoundEffectSystem)world.getSystemManager().getSystem("SoundEffectSystem");
        ses.newSpatialSound(useSoundId, user);

        return true;
    }

    /**
     * 物品被放下来（从手持变成非手持）
     * */
    public void putDown (ItemStack itemStack, World world, LivingEntity holder) {
    }

    /**
     * 当物品被丢弃的时候的行为
     * */
    public void beDropped (ItemStack itemStack, World world, LivingEntity dropper) {
    }

    /**
     * 获取材质
     * <p>
     * 有返回值，以便于有多个材质的物品使用
     * @param texturePath 当此为null时默认之前加载过
     * */
    public TextureRegion getTextureRegion (String id, String texturePath) {
        if (texturePath == null) {
            texturePath = AssetsLoader.getInstance().getPath(id);
        }

        AssetsLoader.getInstance().loadAsync(id, texturePath, Texture.class, null);
        return new TextureRegion(AssetsLoader.getInstance().getById(id, Texture.class));
    }

    /**
     * 直接把物品的texture加载并赋值
     * */
    public void loadTextureRegion (String id, String texturePath) {
        this.texture = this.getTextureRegion(id, texturePath);
    }

    public Property getProperty () {
        return this.property;
    }

    public Item setProperty (Property property) {
        this.property = property;
        return this;
    }

    @Override
    public String getID () {
        return this.id;
    }
    @Override
    public Item setID (String id) {
        this.id = id;
        return this;
    }

    /**
     * 物品的类型
     * */
    public enum Type {
        COMMON,         //普通物品
        CONSUMPTION,    //消耗品
        WEAPON,         //武器
        EQUIPMENT       //装备
    }

    /**
     * 物品的属性
     * */
    public static class Property {
        //属性映射
        private PropertiesDataMap<?> propertiesMap;

        /**
         * 实例化后默认属性
         * */
        public Property () {
            this.setPropertiesMap(ITEM_DEFAULT_PROPERTIES_DATA_MAP.copy());
        }

        public <T> T get (PropertyType<T> propertyType) {
            return getPropertiesMap().get(propertyType);
        }

        public <T> Property add (PropertyType<T> propertyType, T value) {
            getPropertiesMap().add(propertyType, value);
            return this;
        }

        public int getMaxCount () {
            return get(PropertyTypes.ITEM_MAX_COUNT);
        }

        public Property setMaxCount (int maxCount) {
            if (maxCount > 0){
                add(PropertyTypes.ITEM_MAX_COUNT, maxCount);
                return this;
            }
            throw new IllegalArgumentException ("最大堆叠数必须大于0！！！");
        }

        public String getUseSoundId () {
            return get(PropertyTypes.ITEM_USE_SOUND_ID);
        }

        public Property setUseSoundId (String useSoundId) {
            add(PropertyTypes.ITEM_USE_SOUND_ID, useSoundId);
            return this;
        }

        public float getDamage () {
            return get(PropertyTypes.WEAPON_DAMAGE);
        }

        public Property setDamage (float damage) {
            add(PropertyTypes.WEAPON_DAMAGE, damage);
            return this;
        }

        public int getDuration () {
            return get(PropertyTypes.WEAPON_DURATION);
        }

        public Property setDuration (int duration) {
            add(PropertyTypes.WEAPON_DURATION, duration);
            return this;
        }

        public float getUseSpan () {
            return get(PropertyTypes.WEAPON_USE_SAPN);
        }

        public Property setUseSpan (float useSpan) {
            add(PropertyTypes.WEAPON_USE_SAPN, useSpan);
            return this;
        }

        public PropertiesDataMap<?> getPropertiesMap () {
            return this.propertiesMap;
        }

        public Property setPropertiesMap (PropertiesDataMap<?> propertiesMap) {
            this.propertiesMap = propertiesMap;
            return this;
        }

        /**
         * 属性是否相同的判断，判断的是所持有的属性的种类以及值，并不是判断两者是否为同一个实例
         **/
        public boolean equals (Property property) {
            return this.getPropertiesMap().equals(property.getPropertiesMap());
        }
    }
}

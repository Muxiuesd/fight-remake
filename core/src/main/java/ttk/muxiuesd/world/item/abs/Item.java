package ttk.muxiuesd.world.item.abs;

import com.badlogic.gdx.utils.Array;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.CodecBuilder;
import game.muxiuesd.bedrockcore.serialization.Codecable;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.data.JsonPropertiesMap;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.ID;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.interfaces.world.item.ItemUpdateable;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.registry.ItemStackBehaviours;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.serialization.codecs.CodecJsonPropertiesMap;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品的模板类
 * <p>
 * 游戏中一种物品只有一个实例，同一种物品的不同物品堆叠都持有同一个物品实例，对这个物品实例的修改会影响整个游戏的相同物品
 * */
public class Item implements ID<Item>, ItemUpdateable, Codecable<Item> {
    public static final Codec<Item> CODEC = Codec.STRING.xmap(
        Registries.ITEM::get,       // 解码: String -> Item (从注册表获取)
        Item::getID                 // 编码: Item -> String (取id)
    );



    private Identifier identifier;                          //物品的id标识
    private Property property;                              //物品最原始的属性，原则上不直接对这个原始数据进行操作

    public Item () {}

    /**
     * 最普通的物品的构造方法
     * */
    public Item (Property property) {
        this.property = property;
    }

    @Override
    public void update (float delta, ItemStack itemStack) {
    }

    /**
     * 获取物品的词条文本
     * */
    public Array<Text> getTooltips (Array<Text> array, ItemStack itemStack) {
        //TODO 物品自己的词条
        return array;
    }

    /**
     * 使用此物品
     * @return 是否使用成功
     * */
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        //播放物品使用音效
        AudioHolder useSound = this.getProperty().getUseSound();
        SoundSystem ses = world.getSystem(SoundSystem.class);
        ses.playSpatialSound(useSound, user);

        return true;
    }

    /**
     * 物品被放下来（从手持变成非手持）
     * */
    public void putDown (ItemStack itemStack, World world, LivingEntity<?> holder) {
    }

    /**
     * 当物品被丢弃的时候的行为
     * */
    public void beDropped (ItemStack itemStack, World world, LivingEntity<?> dropper) {
    }

    /**
     * 必须实现的类：获取这个物品的行为。
     * <p>
     * 没有物品行为的物品将不能正常被使用
     * */
    public IItemStackBehaviour getBehaviour () {
        //默认是普通物品的物品行为
        return ItemStackBehaviours.COMMON;
    };

    /**
     * 获取物品的属性
     * */
    public Property getProperty () {
        return this.property;
    }

    public Item setProperty (Property property) {
        this.property = property;
        return this;
    }

    @Override
    public String getID () {
        return this.getIdentifier().getID();
    }

    public Identifier getIdentifier () {
        return this.identifier;
    }

    @Override
    public Item setIdentifier (Identifier identifier) {
        //Identifier 只在注册阶段给定，注册过后不允许修改
        if (this.identifier != null && !this.identifier.equals(identifier)) {
            throw new IllegalStateException("Identifier 已设置，禁止修改！物品：" + this.identifier.getID() + " -> " + identifier.getID());
        }
        this.identifier = identifier;
        return this;
    }

    @Override
    public Codec<Item> getCodec () {
        return CODEC;
    }

    /**
     * 物品的属性类
     * */
    public static class Property implements Codecable<Property>{
        public static final Codec<Property> CODEC = CodecBuilder.<Property>create()
            .field("data_map",
                Property::getPropertiesMap,
                Property::setPropertiesMap,
                CodecJsonPropertiesMap.CODEC)
            .noArgFactory(Property::new);


        //属性映射
        private JsonPropertiesMap propertiesMap;

        /**
         * 实例化默认属性
         * */
        public Property () {
            this.setPropertiesMap(new JsonPropertiesMap()
                .add(PropertyTypes.ITEM_MAX_COUNT, 64)
                .add(PropertyTypes.ITEM_ON_USING, false)
                .add(PropertyTypes.ITEM_USE_SOUND, Sounds.ITEM_CLICK)
            );
        }

        /**
         * 获取一个属性，如果不存在这个属性就返回指定的默认值
         * */
        public <T> T get (PropertyType<T> propertyType, T defaultValue) {
            return this.contain(propertyType) ? this.get(propertyType) : defaultValue;
        }

        /**
         * 获取一个属性
         * */
        public <T> T get (PropertyType<T> propertyType) {
            return (T) getPropertiesMap().get(propertyType);
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

        public AudioHolder getUseSound () {
            return get(PropertyTypes.ITEM_USE_SOUND);
        }

        public Property setUseSound (AudioHolder audioHolder) {
            add(PropertyTypes.ITEM_USE_SOUND, audioHolder);
            return this;
        }

        public float getDamage () {
            return get(PropertyTypes.WEAPON_DAMAGE);
        }

        public Property setDamage (float damage) {
            add(PropertyTypes.WEAPON_DAMAGE, damage);
            return this;
        }

        /**
         * 获取物品耐久
         * */
        public int getDuration () {
            return this.get(PropertyTypes.ITEM_DURATION, 0);
        }

        public Property setDuration (int duration) {
            this.add(PropertyTypes.ITEM_DURATION, duration);
            return this;
        }

        public float getUseSpan () {
            return this.get(PropertyTypes.WEAPON_USE_SAPN, 0f);
        }

        public Property setUseSpan (float useSpan) {
            this.add(PropertyTypes.WEAPON_USE_SAPN, useSpan);
            return this;
        }

        /**
         * 获取武器的击退冲击力（0 = 不击退）
         * */
        public float getKnockback () {
            return this.get(PropertyTypes.WEAPON_KNOCKBACK, 0f);
        }

        public Property setKnockback (float knockback) {
            this.add(PropertyTypes.WEAPON_KNOCKBACK, knockback);
            return this;
        }

        /**
         * 检查是否有这个属性
         * */
        public boolean contain (PropertyType<?> type) {
            return this.getPropertiesMap().contain(type);
        }

        public JsonPropertiesMap getPropertiesMap () {
            return this.propertiesMap;
        }

        public Property setPropertiesMap (JsonPropertiesMap propertiesMap) {
            this.propertiesMap = propertiesMap;
            return this;
        }

        /**
         * 属性是否相同的判断，判断的是所持有的属性的种类以及值，并不是判断两者是否为同一个实例
         **/
        public boolean equals (Property property) {
            return this.getPropertiesMap().equals(property.getPropertiesMap());
        }

        /**
         * 复制一份属性
         * */
        public Property copy () {
            return new Property().setPropertiesMap(this.getPropertiesMap().copy());
        }

        @Override
        public Codec<Property> getCodec () {
            return CODEC;
        }
    }
}

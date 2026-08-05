package ttk.muxiuesd.world.block.abs;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonValue;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.CodecBuilder;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.ICatData;
import ttk.muxiuesd.interfaces.Tickable;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.cat.CatInt;
import ttk.muxiuesd.world.cat.CatsHolder;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.genfactory.ItemEntityGetter;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 植物基础抽象类
 * <p>
 * 姑且算方块，使用方块的逻辑
 * */
public abstract class Botany extends Block implements Tickable, ICatData {
    /**
     * 植物的现代化编解码器
     * <p>
     * 解码时通过方块注册表拿到原型，再调用{@link #createSelf}创建一个新的实例
     * */
    public static final Codec<Botany> CODEC = CodecBuilder.<Botany>create()
        .paramField("id", Botany::getID, Codec.STRING)
        .field("growLevel", Botany::getGrowLevel, Botany::setGrowLevel, Codec.INT)
        .field("property",
            botany -> {
                //把当前的cats数据写入属性，保证保存的数据是最新的
                CatsHolder cats = botany.getProperty().get(PropertyTypes.CATS);
                if (cats != null) {
                    botany.writeCatData(cats);
                }
                return botany.getProperty();
            },
            Botany::setProperty,
            Block.Property.CODEC)
        .factory(id -> {
            Block block = Registries.BLOCK.getOrNull(id);
            if (block instanceof Botany botany) return botany.createSelf();
            throw new IllegalArgumentException("方块注册表中不存在植物方块：" + id);
        });

    public static TextureRegion loadTextureRegion (String name) {
        return Util.loadTextureRegion(Fight.ID(name), Fight.BotanyTexturePath("crops/" + name));
    }

    private Item droppedItem;   //植物生长过程中被破坏后的掉落物
    private int growLevel = 0;  //生长等级，每一个生长等级会有不同的贴图
    private TextureRegion[] textureRegions; //不同生长等级的贴图


    public Botany (Property property) {
        super(property);
    }

    /**
     * 生成自己的实例
     * */
    public abstract Botany createSelf ();

    @Override
    public void readCatData (JsonValue values) {
        this.setGrowLevel(values.getInt("growLevel"));
    }

    @Override
    public void writeCatData (CatsHolder holder) {
        holder.put("growLevel", new CatInt(this.getGrowLevel()));
    }

    @Override
    public void beDestroyed (World world, Vector2 position) {
        Item item = this.getDroppedItem();
        if (item == null) return;

        //掉落物品
        EntitySystem es = world.getSystem(EntitySystem.class);
        Vector2 pos = new Vector2(position);
        pos.add(
            MathUtils.random(-0.3f, 0.3f),
            MathUtils.random(-0.3f, 0.3f)
        );
        ItemEntity itemEntity = ItemEntityGetter.get(es, pos, new ItemStack(item, 1));
        itemEntity.setLivingTime(Fight.ITEM_ENTITY_PICKUP_SPAN.getValue());

    }

    public Item getDroppedItem () {
        return this.droppedItem;
    }

    public Botany setDroppedItem (Item droppedItem) {
        this.droppedItem = droppedItem;
        return this;
    }

    /**
     * 生长等级增加某一值
     * */
    public Botany growLevelIncrease (int value) {
        this.setGrowLevel(this.getGrowLevel() + value);
        return this;
    }

    public int getGrowLevel () {
        return this.growLevel;
    }

    public Botany setGrowLevel (int growLevel) {
        if (growLevel >=0 ) this.growLevel = growLevel;
        return this;
    }

    @Override
    public TextureRegion getTextureRegion () {
        return this.getCurGrowLevelTextureRegion();
    }

    /**
     * 获取当前的生长等级对应的贴图材质
     * */
    public TextureRegion getCurGrowLevelTextureRegion () {
        int level = this.getGrowLevel();
        TextureRegion[] regions = this.getTextureRegions();
        if (level > regions.length - 1) {
            //如果生长等级超过对应的贴图，就返回最大的
            return regions[regions.length - 1];
        }
        return regions[level];
    }

    /**
     * 设置每一个生长等级对应的贴图
     * */
    public Botany setGrowLevelTextureRegions (TextureRegion... textureRegion) {
        return this.setTextureRegions(textureRegion);
    }

    public TextureRegion[] getTextureRegions() {
        return this.textureRegions;
    }

    public Botany setTextureRegions (TextureRegion[] textureRegions) {
        this.textureRegions = textureRegions;
        return this;
    }
}

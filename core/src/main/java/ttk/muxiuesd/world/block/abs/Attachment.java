package ttk.muxiuesd.world.block.abs;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.CodecBuilder;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.ICatData;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.cat.CatsHolder;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.genfactory.ItemEntityGetter;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 附着物
 * <p>
 * 附着于其他方块之上的方块，本体是方块，但是不能凭空存在，被破坏就破碎掉落对应的物品
 * <p>
 * 两种实例形态：<br>
 * 共享型：与普通方块一样，全世界只有注册时创建的那一个实例，在不同的坐标上多次渲染（享元模式），
 * 不覆写{@link #createInstance()}，{@link #createInstance()}默认返回自身<br>
 * 每格独享型：与带方块实体的方块一样，每个坐标上都是一个单独的实例，覆写{@link #createInstance()}返回自己的副本，
 * 需要保存每格状态的附着物（如{@link Botany}）走这条
 * <p>
 * 本类不实现{@link ttk.muxiuesd.interfaces.Tickable}：不需要tick更新的附着物就别进入tick列表
 * <p>
 * 能放在哪些方块上不在这里判定，在{@link ttk.muxiuesd.registry.AttachmentPlacements}里登记，
 * 没登记过的附着物一处都放不了，而且一律不能附着在空气方块上
 * */
public abstract class Attachment extends Block {
    /**
     * 附着物的现代化编解码器
     * <p>
     * 共享型与每格独享型共用这一个编解码器：解码时通过方块注册表拿到原型，再交给{@link #createInstance()}决定产出什么
     */
    public static final Codec<Attachment> CODEC = CodecBuilder.<Attachment>create()
        .paramField("id", Attachment::getID, Codec.STRING)
        .field("property",
            attachment -> {
                //把当前的cats数据写入属性，保证保存的数据是最新的
                CatsHolder cats = attachment.getProperty().get(PropertyTypes.CATS);
                //只有每格独享型的附着物（实现了ICatData）才有自己的状态数据需要写入
                if (cats != null && attachment instanceof ICatData catData) {
                    catData.writeCatData(cats);
                }
                return attachment.getProperty();
            },
            (attachment, property) -> {
                //设置属性
                attachment.setProperty(property);
                //把属性中保存的cats数据读取到附着物上
                CatsHolder cats = property.get(PropertyTypes.CATS);
                if (cats != null && attachment instanceof ICatData catData) {
                    catData.readCatData(cats);
                }
            },
            Block.Property.CODEC)
        .factory(Attachment::fromId);

    private Item droppedItem;   //附着物被破坏后的掉落物，为null时什么都不掉

    public Attachment (Property property) {
        super(property);
    }

    /**
     * 由方块id创建附着物实例
     * <p>
     * 共享型附着物全世界一个实例，直接返回注册表里的原型；<br>
     * 每格独享型附着物由{@link #createInstance()}返回自己的副本
     * */
    public static Attachment fromId (String id) {
        Block block = Registries.BLOCK.getOrNull(id);
        if (block instanceof Attachment attachment) return attachment.createInstance();
        throw new IllegalArgumentException("方块注册表中不存在附着物：" + id);
    }

    /**
     * 产生要放入世界的实例
     * <p>
     * 默认就是"共享实例"：自己的实例就是自己，全局共享的附着物不需要覆写；<br>
     * 每格独享的附着物（如植物）覆写本方法，借助{@link #createSelf()}返回一个全新的实例
     * */
    public Attachment createInstance () {
        return this;
    }

    /**
     * 生成自己的实例（只有每格独享的附着物才需要）
     * */
    protected abstract Attachment createSelf ();

    @Override
    public void beDestroyed (World world, Vector2 position) {
        Item item = this.getDroppedItem();
        if (item == null) return;

        //破碎掉落物品
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

    public Attachment setDroppedItem (Item droppedItem) {
        this.droppedItem = droppedItem;
        return this;
    }
}

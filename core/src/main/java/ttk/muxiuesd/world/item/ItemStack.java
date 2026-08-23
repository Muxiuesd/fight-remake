package ttk.muxiuesd.world.item;

import com.badlogic.gdx.utils.Array;
import game.muxiuesd.bedrockcore.app.interfaces.Updateable;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.CodecBuilder;
import game.muxiuesd.bedrockcore.serialization.Codecable;
import game.muxiuesd.bedrockcore.util.Timer;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.registry.ItemStackBehaviours;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.abs.Weapon;

/**
 * 物品堆栈
 * <p>
 * 物品传进来后会复制一份属性数据进物品堆叠里面持有，对物品堆叠里的物品属性进行修改不会影响原本的物品实例
 * */
public class ItemStack implements Updateable, Codecable<ItemStack> {
    public static final Codec<ItemStack> CODEC = CodecBuilder.<ItemStack>create()
        .field("item", ItemStack::getItem, ItemStack::setItem, Item.CODEC)
        .field("amount", ItemStack::getAmount, ItemStack::setAmount, Codec.INT)
        .field("property", ItemStack::getProperty, ItemStack::setProperty, Item.Property.CODEC)
        .noArgFactory(ItemStack::new);


    /// 空物品堆叠
    public static final ItemStack VOID = new ItemStack();


    private Item item;//所持有的物品
    private IItemStackBehaviour behaviour;//物品堆叠所用的行为，一般来说根据物品的类型来判断
    private Item.Property property;//物品堆叠所持有的物品属性，与物品本身自带的属性不是一个实例
    private int amount;         //数量
    public Timer<?> useTimer;   //使用时间计时器

    /**
     * 给空物品使用的构造方法，啥也没有
     * */
    private ItemStack () {
        this.item = null;
        this.behaviour = null;
    }
    public ItemStack (Item item) {
        //不指定数量就默认这个物品的最大数量
        this(item, item.getProperty().getMaxCount());
    }
    public ItemStack (Item item, int amount) {
        //没有指定物品属性就获取复制的
        this(item, amount, item.getBehaviour(), item.getProperty().copy());
    }
    //需要指定物品属性
    public ItemStack (Item item, int amount, Item.Property property) {
        this(item, amount, item.getBehaviour(), property);
    }
    public ItemStack (Item item, int amount, IItemStackBehaviour behaviour, Item.Property property) {
        this.setItem(item);
        if (behaviour != null) this.behaviour = behaviour;
        else this.behaviour = ItemStackBehaviours.COMMON;   //防止null
        this.property = property;
        this.amount = Math.max(amount, 1);  //防止物品数量低于1
    }

    /**
     * 使用
     * */
    public boolean use (World world, LivingEntity<?> user) {
        return this.behaviour.use(world, user, this);
    }

    /**
     * 这个物品堆叠从手上换下来
     * */
    public void putDown (World world, LivingEntity<?> holder) {
        this.getItem().putDown(this, world, holder);
    }

    @Override
    public void update (float delta) {
        //更新物品
        this.getItem().update(delta, this);
        if (this.useTimer != null) this.useTimer.update(delta);
    }

    /**
     * 获取物品的词条文本
     * */
    public Array<Text> getTooltips () {
        Array<Text> array = new Array<>();
        //基础词条
        array.add(Text.ofItem(this.getItem().getID()));  //物品名称

        //物品自定义词条
        this.getItem().getTooltips(array, this);

        //基础词条
        //持有耐久属性就添加词条
        if (this.getItem().getProperty().contain(PropertyTypes.ITEM_DURATION)) {
            array.add(
                Text.ofText(Fight.ID("item_duration"))
                    .set(0, this.getProperty().getDuration())
                    .set(1, this.getItem().getProperty().getDuration())
            );
        }
        return array;
    }


    /**
     * 指定数量的复制，数量最少为1
     * <p>
     * 复制过后的物品堆叠所持有的属性与原来的相同，原物品堆叠数量不减少
     * */
    public ItemStack copy (int amount) {
        int newAmount = Math.max(amount, 1);
        if (amount > this.getProperty().getMaxCount()) newAmount = this.getProperty().getMaxCount();

        return new ItemStack(this.getItem(), newAmount, this.behaviour, this.getProperty().copy());
    }

    /**
     * 分开物品堆叠
     * <p>
     * 分离出指定数量，原物品堆叠数量会减少；无法分离时返回空物品堆叠
     * */
    public ItemStack split (int amount) {
        if (amount <= 0) return ItemStack.VOID;
        ItemStack newStack;
        //超过或者等于最大数量，直接返回目前的数量
        if (amount >= this.getAmount()) {
            newStack = new ItemStack(this.getItem(), this.getAmount(), this.behaviour, this.copyProperty());
            this.setAmount(0);
        }else {
            //没达到最大数量
            newStack = new ItemStack(this.getItem(), amount, this.behaviour, this.copyProperty());
            this.amountDecrease(amount);
        }
        return newStack;
    }

    /**
     * 物品堆叠的合并
     * @param stack 需要被合并的物品堆叠
     * */
    public void merge (ItemStack stack) {
        //物品相同就执行合并
        if (this.equals(stack)) {
            int stackAmount = stack.getAmount();
            int maxCount = stack.getProperty().getMaxCount();
            int newAmount = this.getAmount() + stackAmount;
            if (newAmount > maxCount) {
                //要是超出堆叠上限，传入的物品堆叠的数量变为超出的部分
                this.amount = maxCount;
                stack.setAmount(newAmount - maxCount);
            }else {
                //合并后没超出堆叠上限
                this.amount = newAmount;
                stack.setAmount(0);
            }
        }
    }

    /**
     * 检测两个物品堆叠是否相同，需要所持有的物品以及属性（数量，种类，值）相同
     * */
    public boolean equals (ItemStack stack) {
        //空堆叠（VOID/null）只与空堆叠相等
        if (this.isVoid()) return stack == null || stack.isVoid();
        if (stack == null || stack.isVoid()) return false;
        //物品不是一种就无需判断直接false
        if (this.getItem() != stack.getItem()) return false;
        //比较所持有的属性
        return this.getProperty().equals(stack.getProperty());
    }

    /**
     * 覆写 Object.equals（委托给 {@link #equals(ItemStack)}），
     * 使 List/Map 的 contains/remove 等操作遵循属性全等语义
     * */
    @Override
    public boolean equals (Object obj) {
        if (this == obj) return true;
        if (obj instanceof ItemStack stack) {
            return this.equals(stack);
        }
        return false;
    }

    /**
     * 与 {@link #equals(Object)} 保持契约一致
     * <p>
     * 基于物品实例的哈希（属性全等的堆叠物品实例相同 → 哈希相同；
     * 属性不同但物品相同 → 哈希可相同，不违反 equals/hashCode 契约）
     * */
    @Override
    public int hashCode () {
        return this.getItem() != null ? this.getItem().hashCode() : 0;
    }

    /**
     * 这个物品堆叠是否是空的（空物品堆叠 {@link #VOID}）
     * <p>
     * 空槽位/空手的统一判断，替代 null 判断
     * */
    public boolean isVoid () {
        return this == VOID;
    }

    /**
     * 物品是否在使用中
     * */
    public boolean onUsing () {
        return this.getProperty().get(PropertyTypes.ITEM_ON_USING, false);
    }

    /**
     * 获取持有的物品实例
     * */
    public Item getItem () {
        return this.item;
    }

    /**
     * 设置新的物品实例
     * */
    public ItemStack setItem (Item item) {
        this.item = item;
        this.behaviour = item.getBehaviour();
        this.setProperty(item.getProperty().copy());

        if (item instanceof Weapon weapon) {
            this.useTimer = new Timer<>(weapon.getProperty().getUseSpan());
        }
        return this;
    }

    /**
     * 复制这个物品堆栈的物品属性
     * */
    public Item.Property copyProperty () {
        return this.getProperty().copy();
    }

    public Item.Property getProperty () {
        return this.property;
    }

    public ItemStack setProperty (Item.Property property) {
        this.property = property;
        return this;
    }

    public int getAmount () {
        return this.amount;
    }

    public ItemStack setAmount (int amount) {
        //空堆叠无属性，直接返回（getProperty() 为 null）
        if (this.isVoid()) return this;
        //上限保护：数量不能超过堆叠上限（原两个独立 if 会让超上限值覆盖上限，堆叠数量可突破上限）；负数忽略
        if (amount >= 0) {
            this.amount = Math.min(amount, this.getProperty().getMaxCount());
        }
        return this;
    }

    /**
     * 快速减少一个数量
     * */
    public ItemStack amountFastDecrease () {
        return this.amountDecrease(1);
    }

    /**
     * 快速增加一个数量
     * */
    public ItemStack amountFastIncrease () {
        return this.amountIncrease(1);
    }

    /**
     * 减少指定数量
     * */
    public ItemStack amountDecrease (int amount) {
        this.setAmount(this.getAmount() - amount);
        return this;
    }

    /**
     * 增加指定数量
     * */
    public ItemStack amountIncrease (int amount) {
        this.setAmount(this.getAmount() + amount);
        return this;
    }

    /**
     * 减少指定的耐久值
     * */
    public ItemStack durationDecrease (int value) {
        if (this.getProperty().contain(PropertyTypes.ITEM_DURATION)
            && this.getProperty().getDuration() > 0) {
            int d = this.getProperty().getDuration() - value;
            this.getProperty().setDuration(Math.max(d, 0));
        }
        return this;
    }

    /**
     * 增加指定的耐久值
     * */
    public ItemStack durationIncrease (int value) {
        if (this.getProperty().contain(PropertyTypes.ITEM_DURATION)) {
            int maxDuration = this.getItem().getProperty().getDuration();
            //确保不会超过耐久上限
            int d = this.getProperty().getDuration() + value;
            this.getProperty().setDuration(Math.min(d, maxDuration));
        }
        return this;
    }

    /**
     * 堆叠数量是否最大
     * */
    public boolean isFull() {
        return this.getAmount() >= this.getProperty().getMaxCount();
    }

    public boolean isReady () {
        return this.useTimer != null && this.useTimer.isReady();
    }


    @Override
    public Codec<ItemStack> getCodec () {
        return CODEC;
    }
}

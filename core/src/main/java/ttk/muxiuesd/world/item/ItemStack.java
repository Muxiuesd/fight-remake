package ttk.muxiuesd.world.item;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.data.abs.PropertiesDataMap;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.util.Timer;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.abs.Weapon;
import ttk.muxiuesd.world.item.stack.behaviour.ItemStackBehaviours;
import ttk.muxiuesd.world.item.stack.behaviour.WeaponItemStackBehaviour;

/**
 * 物品堆栈
 * <p>
 * 物品传进来后会复制一份属性数据进物品堆叠里面持有，对物品堆叠里的物品属性进行修改不会影响原本的物品实例
 * */
public class ItemStack implements Updateable, ShapeRenderable {
    //所持有的物品
    private final Item item;
    //物品堆叠所持有的物品属性，与物品本身自带的属性不是一个实例
    private Item.Property property;
    //数量
    private int amount;
    //物品堆叠所用的行为，一般来说根据物品的类型来判断
    private final IItemStackBehaviour behaviour;
    //使用时间计时器
    public Timer useTimer;

    public ItemStack (Item item) {
        //不指定数量就默认这个物品的最大数量
        this(item, item.property.getMaxCount());
    }
    public ItemStack (Item item, int amount) {
        this(item, amount, ItemStackBehaviours.create(item.type), item.property.getPropertiesMap().copy());
    }
    public ItemStack (Item item, int amount, PropertiesDataMap<?> propertiesMap) {
        this(item, amount, ItemStackBehaviours.create(item.type), propertiesMap);
    }
    public ItemStack (Item item, int amount, IItemStackBehaviour behaviour, PropertiesDataMap<?> propertiesMap) {
        this.item = item;
        this.amount = amount;
        this.behaviour = behaviour;
        this.property = new Item.Property() //将原物品的属性复制一份
            .setPropertiesMap(propertiesMap.copy());

        if (behaviour instanceof WeaponItemStackBehaviour) {
            Weapon weapon = (Weapon) item;
            this.useTimer = new Timer(weapon.getProperty().getUseSpan());
        }
    }

    /**
     * 使用
     * */
    public boolean use (World world, LivingEntity user) {
        return this.behaviour.use(world, user, this);
    }

    /**
     * 这个物品堆叠从手上换下来
     * */
    public void putDown (World world, LivingEntity holder) {
        this.getItem().putDown(this, world, holder);
    }

    @Override
    public void update (float delta) {
        //更新物品
        this.getItem().update(delta, this);
        if (this.useTimer != null) this.useTimer.update(delta);
    }

    /**
     * 物品在手上的绘制
     * */
    public void drawItemOnHand (Batch batch, LivingEntity holder) {
        this.getItem().drawOnHand(batch, holder, this);
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        this.getItem().renderShape(batch, this);
    }

    /**
     * 指定数量的复制
     * <p>
     * 复制过后的物品堆叠所持有的属性与原来的相同
     * */
    public ItemStack copy (int amount) {
        if (amount <= 0) return null;
        //超过或者等于最大数量
        if (amount >= this.getAmount()) {
            this.setAmount(0);
            return new ItemStack(this.getItem(), this.getAmount(), this.behaviour, this.property.getPropertiesMap());
        }
        //没达到最大数量
        ItemStack newStack = new ItemStack(this.getItem(), amount, this.behaviour, this.property.getPropertiesMap());
        this.setAmount(this.getAmount() - amount);
        return newStack;
    }

    /**
     * 检测两个物品堆叠是否相同，需要所持有的物品以及属性（数量，种类，值）相同
     * */
    public boolean equals (ItemStack stack) {
        if (stack == null) return false;
        //物品不是一种就无需判断直接false
        if (this.getItem() != stack.getItem()) return false;
        //比较所持有的属性
        return this.getProperty().equals(stack.getProperty());
    }

    /**
     * 物品是否在使用中
     * */
    public boolean onUsing () {
        return this.getProperty().get(PropertyTypes.ITEM_ON_USING);
    }


    public Item getItem () {
        return this.item;
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
        if (amount >= this.getProperty().getMaxCount()) this.amount = this.getProperty().getMaxCount();
        if (amount >= 0) this.amount = amount;
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


}

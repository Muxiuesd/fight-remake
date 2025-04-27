package ttk.muxiuesd.world.item;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import ttk.muxiuesd.interfaces.IItemStackBehaviour;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.util.Timer;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.abs.Weapon;
import ttk.muxiuesd.world.item.stack.behaviour.ItemStackBehaviourFactory;
import ttk.muxiuesd.world.item.stack.behaviour.WeaponItemStackBehaviour;

/**
 * 物品堆栈
 * */
public class ItemStack implements Updateable {
    private final Item item;
    private Item.Property property;
    private int amount;
    private float useSpan;
    private final IItemStackBehaviour behaviour;
    public Timer useTimer;  //使用计时器


    public ItemStack (Item item) {
        //不指定数量就默认这个物品的最大数量
        this(item, item.property.getMaxCount());
        try {
            this.property = ClassReflection.newInstance(getItem().property.getClass());
        } catch (ReflectionException e) {
            throw new RuntimeException(e);
        }
    }
    public ItemStack (Item item, int amount) {
        this(item, amount, ItemStackBehaviourFactory.create(item.type));
    }
    public ItemStack (Item item, int amount, IItemStackBehaviour behaviour) {
        this.item = item;
        this.amount = amount;
        this.behaviour = behaviour;
        if (behaviour instanceof WeaponItemStackBehaviour) {
            Weapon weapon = (Weapon) item;
            this.useTimer = new Timer(weapon.getProperties().getUseSpan());
        }
    }


    /**
     * 使用
     * */
    public boolean use (World world, LivingEntity user) {
        return this.behaviour.use(world, user, this);
    }

    @Override
    public void update (float delta) {
        //更新物品
        this.getItem().update(delta);
        if (this.useTimer != null) this.useTimer.update(delta);

        //武器冷却时间更新
        /*if (this.getItem() instanceof Weapon weapon) {
            this.useSpan += delta;
            float maxSpan = weapon.getProperties().getUseSpan();
            if (this.useSpan > maxSpan) {
                this.useSpan = maxSpan + 1f;
            }
        }*/
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

    public void setAmount (int amount) {
        if (amount > 0) this.amount = amount;
    }

    public boolean isReady () {
        return this.useTimer != null && this.useTimer.isReady();
    }
}

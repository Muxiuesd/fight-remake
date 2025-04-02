package ttk.muxiuesd.world.item;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import ttk.muxiuesd.interfaces.IItemStackBehaviour;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.LivingEntity;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.abs.Weapon;
import ttk.muxiuesd.world.item.stack.behaviour.CommonItemStackBehaviour;
import ttk.muxiuesd.world.item.stack.behaviour.ConsumptionItemStackBehaviour;
import ttk.muxiuesd.world.item.stack.behaviour.EquipmentItemStackBehaviour;

/**
 * 物品堆栈
 * */
public class ItemStack implements Updateable {
    private final Item item;
    private Item.Property property;
    private int amount;
    private float useSpan;
    private IItemStackBehaviour behaviour;

    public ItemStack(Item item) {
        //默认直接最大堆叠值
        this(item, item.property.getMaxCount());
        try {
            this.property = ClassReflection.newInstance(getItem().property.getClass());
        } catch (ReflectionException e) {
            throw new RuntimeException(e);
        }
        //根据不同的物品类型来注入行为
        if (this.getItem().type == Item.Type.COMMON) {
            this.behaviour = new CommonItemStackBehaviour();
        }else if (this.getItem().type == Item.Type.CONSUMPTION) {
            this.behaviour = new ConsumptionItemStackBehaviour();
        }else if (this.getItem().type == Item.Type.WEAPON) {
            this.behaviour = new WeaponItemStackBehaviour();
        } else if (this.getItem().type == Item.Type.EQUIPMENT) {
            this.behaviour = new EquipmentItemStackBehaviour();
        }
    }
    public ItemStack (Item item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    /**
     * 使用
     * */
    public boolean use (World world, LivingEntity user) {
        return this.behaviour.use(world, user, this);
    }

    @Override
    public void update (float delta) {
        if (this.getItem() instanceof Weapon) {
            Weapon weapon = (Weapon) this.getItem();
            this.useSpan += delta;
            float maxSpan = weapon.getProperties().getUseSpan();
            if (this.useSpan > maxSpan) {
                this.useSpan = maxSpan + 1f;
            }
        }
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

    public float getUseSpan () {
        return this.useSpan;
    }

    public ItemStack setUseSpan (float useSpan) {
        this.useSpan = useSpan;
        return this;
    }
}

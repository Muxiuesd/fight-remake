package ttk.muxiuesd.world.item;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.LivingEntity;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.abs.Weapon;

/**
 * 物品堆栈
 * */
public class ItemStack {
    private final Item item;
    private Item.Property property;
    private int amount;
    private float useSpan;


    public ItemStack(Item item) {
        //默认直接最大堆叠值
        this(item, item.property.getMaxCount());
        try {
            this.property = ClassReflection.newInstance(getItem().property.getClass());
        } catch (ReflectionException e) {
            throw new RuntimeException(e);
        }
    }
    public ItemStack (Item item, int amount) {
        this.item = item;
        this.amount = amount;
    }

    /**
     * 使用
     * TODO 使用行为依赖注入，避免判断
     * */
    public boolean use (World world, LivingEntity user) {
        if (this.getItem().type == Item.Type.COMMON) {
            return this.getItem().use(world, user);
        }
        if (this.getItem().type == Item.Type.CONSUMPTION) {
            boolean used = this.getItem().use(world, user);
            if (!used) {
                return false;
            }
            if (this.getAmount() - 1 > 0) {
                //用一次数量减一
                this.setAmount(this.getAmount() - 1);
            }else {
                //数量用光了
                user.backpack.clear(this);
            }
        }else if (this.getItem().type == Item.Type.WEAPON && this.getItem() instanceof Weapon) {
            Weapon weapon = (Weapon) this.getItem();
            boolean used = weapon.use(world, user);
            if (!used) {
                return false;
            }
            Weapon.WeaponProperties properties = (Weapon.WeaponProperties) this.property;
            //用一次耐久减一
            properties.setDuration(properties.getDuration() - 1);
        } else if (this.getItem().type == Item.Type.EQUIPMENT) {
            //TODO 装备使用
        }
        //到这里说明不论何种类型都使用成功
        return true;
    }


    public Item getItem () {
        return this.item;
    }

    public int getAmount () {
        return amount;
    }

    public void setAmount (int amount) {
        if (amount > 0) this.amount = amount;
    }
}

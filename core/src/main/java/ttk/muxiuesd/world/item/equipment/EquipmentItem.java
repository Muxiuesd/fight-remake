package ttk.muxiuesd.world.item.equipment;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.registry.ItemStackBehaviours;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 装备物品
 * TODO 装备的装备属性效果
 * */
public class EquipmentItem extends Item {
    /// 本装备物品持有的装备类型（注册实例），由 Builder 在构造时注入，之后不可改
    private final EquipmentType equipmentType;

    public EquipmentItem (EquipmentType equipmentType, Property property) {
        super(property);
        this.equipmentType = equipmentType;
    }

    /**
     * 获取本装备持有的装备类型
     * <p>
     * 装备槽位持有的也是"注册实例"，两者是同一实例时该装备才能放入该槽位。
     * */
    public EquipmentType getEquipmentType () {
        return this.equipmentType;
    }

    @Override
    public Array<Text> getTooltips (Array<Text> array, ItemStack itemStack) {
        array.add(Text.ofText(Fight.ID("damage_reduction")).set(0, itemStack.getProperty().get(PropertyTypes.DAMAGE_REDUCTION)));
        return super.getTooltips(array, itemStack);
    }

    @Override
    public IItemStackBehaviour getBehaviour () {
        return ItemStackBehaviours.EQUIPMENT;
    }
}

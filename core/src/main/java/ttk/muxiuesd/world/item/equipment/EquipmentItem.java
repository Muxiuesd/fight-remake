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
    /// 装备类型
    public enum Type{
        HELMET,     //头盔
        CHESTPLATE, //胸甲
        LEGGINGS,   //腿甲
        BOOTS,      //靴子
        OTHERS      //其他类型
    }
    //装备的类型
    public final EquipmentItem.Type equipmentType;

    public EquipmentItem (EquipmentItem.Type equipmentType, Property property) {
        super(property);
        this.equipmentType = equipmentType;
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

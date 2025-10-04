package ttk.muxiuesd.world.item.equipment;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.JsonPropertiesMap;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.registry.ItemStackBehaviours;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
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

    public static final JsonPropertiesMap EQUIPMENT_DEFAULT_PROPERTIES_DATA_MAP = new JsonPropertiesMap()
        .add(PropertyTypes.ITEM_MAX_COUNT, 1)
        .add(PropertyTypes.ITEM_ON_USING, false)
        .add(PropertyTypes.ITEM_USE_SOUND_ID, Sounds.EQUIP.getId());

    /**
     * 创建默认的装备属性类
     * */
    public static Property createDefaultProperty () {
        return new Property().setPropertiesMap(EQUIPMENT_DEFAULT_PROPERTIES_DATA_MAP.copy());
    }

    //装备的类型
    public final EquipmentItem.Type equipmentType;

    public EquipmentItem (EquipmentItem.Type equipmentType, Property property, String textureName) {
        this(equipmentType, property, Fight.ID(textureName), Fight.ItemTexturePath(textureName + ".png"));
    }

    public EquipmentItem (EquipmentItem.Type equipmentType, Property property, String textureId, String texturePath) {
        super(Item.Type.EQUIPMENT, property, textureId, texturePath);
        this.equipmentType = equipmentType;
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        return super.use(itemStack, world, user);
    }

    @Override
    public IItemStackBehaviour getBehaviour () {
        return ItemStackBehaviours.EQUIPMENT;
    }
}

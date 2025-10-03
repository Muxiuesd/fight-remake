package ttk.muxiuesd.world.item.equipment;

import ttk.muxiuesd.Fight;
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
 * TODO 装备的装备效果
 * */
public class EquipmentItem extends Item {

    /**
     * 创建默认的装备属性类
     * */
    public static Property createDefaultProperty () {
        return new Property()
            .add(PropertyTypes.ITEM_USE_SOUND_ID, Sounds.EQUIP.getId());
    }

    public EquipmentItem (Property property, String textureName) {
        this(property, Fight.ID(textureName), Fight.ItemTexturePath(textureName + ".png"));
    }

    public EquipmentItem (Property property, String textureId, String texturePath) {
        super(Type.EQUIPMENT, property, textureId, texturePath);
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

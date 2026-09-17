package ttk.muxiuesd.world.item.equipment;

import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.builder.ItemBuilder;

/**
 * 装备物品构建器
 * <p>
 * 装备的类型通过 {@link EquipmentItem.Type} 指定。
 */
public class EquipmentItemBuilder implements ItemBuilder<EquipmentItem> {
    private final EquipmentItem.Type type;
    private AudioHolder equipSound = Sounds.EQUIP;
    private float damageReduction = 0.0f;


    private EquipmentItemBuilder (EquipmentItem.Type type) {
        this.type = type;
    }

    /**
     * 创建装备构建器，指定装备类型
     * */
    public static EquipmentItemBuilder create (EquipmentItem.Type type) {
        return new EquipmentItemBuilder(type);
    }

    @Override
    public EquipmentItem build () {
        return new EquipmentItem(this.type, this.createProperty());
    }

    /**
     * 创建默认的装备属性类
     * */
    public Item.Property createProperty () {
        return new Item.Property()
            .add(PropertyTypes.ITEM_MAX_COUNT, 1)
            .add(PropertyTypes.ITEM_USE_SOUND, this.getEquipSound())
            .add(PropertyTypes.DAMAGE_REDUCTION, this.getDamageReduction());
    }

    public AudioHolder getEquipSound () {
        return this.equipSound;
    }

    public EquipmentItemBuilder setEquipSound (AudioHolder equipSound) {
        this.equipSound = equipSound;
        return this;
    }

    public float getDamageReduction () {
        return this.damageReduction;
    }

    public EquipmentItemBuilder setDamageReduction (float damageReduction) {
        this.damageReduction = damageReduction;
        return this;
    }
}

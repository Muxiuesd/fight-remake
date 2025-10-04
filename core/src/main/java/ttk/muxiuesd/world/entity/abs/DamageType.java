package ttk.muxiuesd.world.entity.abs;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 伤害类型
 * */
public abstract class DamageType <S, T extends LivingEntity<?>> {
    Identifier identifier;
    public DamageType (Identifier identifier) {
        this.identifier = identifier;
    }

    /**
     * 应用伤害类型
     * */
    public abstract void apply (S source, T target);

    /**
     * 计算被攻击者的减伤程度
     * */
    public float computeDamageReduction (S source, T target) {
        Backpack equipmentBackpack = target.getEquipmentBackpack();
        float reduction = 0f;
        for (int i = 0; i < equipmentBackpack.getSize(); i++) {
            ItemStack equipmentItemStack = equipmentBackpack.getItemStack(i);
            if (equipmentItemStack != null)
                reduction += equipmentItemStack.getProperty().get(PropertyTypes.DAMAGE_REDUCTION);
        }
        return reduction;
    }

}

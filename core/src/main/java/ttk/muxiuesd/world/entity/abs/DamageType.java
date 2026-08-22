package ttk.muxiuesd.world.entity.abs;

import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.world.entity.Backpack;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 注册类：伤害类型
 * <p>
 * 用来控制实体之间的伤害计算以及对应的逻辑
 * @param <S> 伤害来源类（任意伤害来源）
 * @param <T> 受到伤害的目标（只有活物实体才能受伤害）
 * */
public abstract class DamageType <S, T extends LivingEntity<?>> {
    private final Identifier identifier;

    public DamageType (Identifier identifier) {
        this.identifier = identifier;
    }

    /**
     * 应用伤害类型
     * */
    public abstract void apply (S source, T target);

    /**
     * 计算该伤害造成的击退力度（0 = 不击退）
     * <p>
     * 默认不击退，需要击退的伤害类型覆写此方法
     * */
    public float computeKnockback (S source) {
        return 0f;
    }

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

    public Identifier getIdentifier () {
        return this.identifier;
    }
}

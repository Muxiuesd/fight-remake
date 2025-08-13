package ttk.muxiuesd.world.entity.abs;

import ttk.muxiuesd.id.Identifier;

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
}

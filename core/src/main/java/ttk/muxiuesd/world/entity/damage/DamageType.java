package ttk.muxiuesd.world.entity.damage;

import ttk.muxiuesd.id.Identifier;

/**
 * 伤害类型
 * */
public abstract class DamageType <S, T> {
    Identifier identifier;
    public DamageType (Identifier identifier) {
        this.identifier = identifier;
    }

    /**
     * 应用伤害类型
     * */
    public abstract void apply (S source, T target);
}

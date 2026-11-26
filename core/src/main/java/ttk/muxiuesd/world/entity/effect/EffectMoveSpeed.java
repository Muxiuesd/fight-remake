package ttk.muxiuesd.world.entity.effect;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.world.entity.abs.AttributesModifier;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.abs.StatusEffect;

/**
 * 速度提升效果
 * <p>
 * 只改实体的当前速度，原始速度一直留在 {@link AttributesModifier} 里，
 * 效果结束直接还原，所以中途存档再读档也能恢复出正确的原始速度
 * */
public class EffectMoveSpeed extends StatusEffect {
    public EffectMoveSpeed (Identifier identifier) {
        super(identifier, Fight.UITexturePath("effect/speed.png"));
    }

    @Override
    public void beginApplyEffect (LivingEntity<?> entity, int level) {
        AttributesModifier attributes = entity.getProperty().getAttributesModifier();
        float baseSpeed = attributes.getBase(PropertyTypes.ENTITY_MOVE_SPEED, 0f);
        //等级 1/2/3 分别提升到 1.5 / 2.5 / 3.5 倍
        attributes.set(PropertyTypes.ENTITY_MOVE_SPEED, baseSpeed * (level + 0.5f));
    }

    @Override
    public void endApplyEffect (LivingEntity<?> entity, int level) {
        entity.getProperty().getAttributesModifier().restore(PropertyTypes.ENTITY_MOVE_SPEED);
    }
}

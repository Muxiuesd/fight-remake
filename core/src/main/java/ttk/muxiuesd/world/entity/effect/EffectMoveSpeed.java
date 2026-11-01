package ttk.muxiuesd.world.entity.effect;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.abs.StatusEffect;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 速度提升效果
 * */
public class EffectMoveSpeed extends StatusEffect {
    //TODO 这种写法有问题，保存游戏后再进入原始速度无法读取到
    private final ConcurrentHashMap<LivingEntity<?>, Float> originalMoveSpeedMap;

    public EffectMoveSpeed (Identifier identifier) {
        super(identifier, Fight.UITexturePath("effect/speed.png"));
        this.originalMoveSpeedMap = new ConcurrentHashMap<>();
    }

    @Override
    public void beginApplyEffect (LivingEntity<?> entity, int level) {
        float originalSpeed = entity.getSpeed();
        float newSpeed = (float) (originalSpeed * (level + 0.5));
        entity.setSpeed(newSpeed);

        this.originalMoveSpeedMap.put(entity, originalSpeed);
    }

    @Override
    public void endApplyEffect (LivingEntity<?> entity, int level) {
        Float originalSpeed = this.originalMoveSpeedMap.remove(entity);
        if (originalSpeed == null) return;
        entity.setSpeed(originalSpeed);
    }
}

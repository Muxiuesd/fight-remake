package ttk.muxiuesd.pool.particle;

import com.badlogic.gdx.graphics.Color;
import ttk.muxiuesd.world.particle.ParticleSpell;

/**
 * 对象池：剑气粒子
 * */
public class ParticleSpellPool extends ParticlePool<ParticleSpell> {
    public ParticleSpellPool () {
        super(DEFAULT_POOL_SIZE);
    }

    @Override
    protected ParticleSpell newObject () {
        ParticleSpell spell = new ParticleSpell();
        spell.init();
        spell.getLight().setColor(new Color(0.9f, 0.1f, 0.3f, 0.6f));
        return spell;
    }
}

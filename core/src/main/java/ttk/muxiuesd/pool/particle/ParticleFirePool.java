package ttk.muxiuesd.pool.particle;

import com.badlogic.gdx.graphics.Color;
import ttk.muxiuesd.world.particle.ParticleFire;

/**
 * 对象池：火焰粒子
 * */
public class ParticleFirePool extends ParticlePool<ParticleFire> {
    public static Color COLOR = new Color(0.88f, 0.1f, 0.1f, 0.2f);;

    public ParticleFirePool () {
        super(DEFAULT_POOL_SIZE);
    }

    @Override
    protected ParticleFire newObject () {
        ParticleFire particle = new ParticleFire(COLOR, 0.2f);
        particle.init();
        return particle;
    }
}

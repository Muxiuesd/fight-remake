package ttk.muxiuesd.world.particle;

import com.badlogic.gdx.utils.Pool;
import ttk.muxiuesd.world.particle.abs.Particle;

/**
 * 粒子池
 * */
public abstract class ParticlePool<T extends Particle> extends Pool<T> {

    public ParticlePool (int initialCapacity) {
        super(initialCapacity);
    }

}

package ttk.muxiuesd.pool.particle;

import com.badlogic.gdx.utils.Pool;
import ttk.muxiuesd.world.particle.abs.Particle;

/**
 * 粒子池
 * */
public abstract class ParticlePool<T extends Particle> extends Pool<T> {
    public static final int DEFAULT_POOL_SIZE = 50;

    public ParticlePool (int initialCapacity) {
        super(initialCapacity);
    }

}

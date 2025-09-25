package ttk.muxiuesd.pool.particle;

import ttk.muxiuesd.world.particle.ParticleBubble;

/**
 * 对象池：气泡粒子
 * */
public class ParticleBubblePool extends ParticlePool<ParticleBubble> {
    public ParticleBubblePool () {
        super(DEFAULT_POOL_SIZE);
    }

    @Override
    protected ParticleBubble newObject () {
        ParticleBubble particle = new ParticleBubble();
        particle.init();
        return particle;
    }
}

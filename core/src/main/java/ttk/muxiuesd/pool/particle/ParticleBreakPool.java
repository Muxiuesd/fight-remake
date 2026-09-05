package ttk.muxiuesd.pool.particle;

import ttk.muxiuesd.world.particle.BlockBreakParticle;

/**
 * 对象池：方块破坏残渣粒子
 * */
public class ParticleBreakPool extends ParticlePool<BlockBreakParticle> {
    public ParticleBreakPool () {
        super(DEFAULT_POOL_SIZE);
    }

    @Override
    protected BlockBreakParticle newObject () {
        BlockBreakParticle particle = new BlockBreakParticle();
        particle.init();
        return particle;
    }
}

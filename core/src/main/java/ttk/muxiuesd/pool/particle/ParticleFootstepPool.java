package ttk.muxiuesd.pool.particle;

import ttk.muxiuesd.world.particle.ParticleFootstep;

/**
 * 对象池：玩家行走脚下粒子
 * */
public class ParticleFootstepPool extends ParticlePool<ParticleFootstep> {
    public ParticleFootstepPool () {
        super(DEFAULT_POOL_SIZE);
    }

    @Override
    protected ParticleFootstep newObject () {
        ParticleFootstep particle = new ParticleFootstep();
        particle.init();
        return particle;
    }
}

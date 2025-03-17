package ttk.muxiuesd.world.particle.motion;

import ttk.muxiuesd.world.particle.abs.Particle;

/**
 * 粒子的大小变换
 * */
public class PmcSizeTrans implements ParticleMotionComp{
    @Override
    public void motion (Particle particle, float delta) {
        float t = particle.lifetime / particle.duration;
        particle.curSize.x = particle.startSize.x + (particle.endSize.x - particle.startSize.x) * t;
        particle.curSize.y = particle.startSize.y + (particle.endSize.y - particle.startSize.y) * t;
    }
}

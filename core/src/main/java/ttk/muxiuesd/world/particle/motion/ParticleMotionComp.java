package ttk.muxiuesd.world.particle.motion;

import ttk.muxiuesd.world.particle.abs.Particle;

/**
 * 粒子运动逻辑组件，简称：Pmc
 * */
public interface ParticleMotionComp {
    void motion (Particle particle, float delta);
}

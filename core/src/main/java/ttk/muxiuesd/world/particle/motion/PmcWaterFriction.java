package ttk.muxiuesd.world.particle.motion;

import ttk.muxiuesd.world.particle.abs.Particle;

/**
 * 粒子的水体阻力
 * */
public class PmcWaterFriction implements ParticleMotionComp{
    @Override
    public void motion (Particle particle, float delta) {
        particle.velocity.scl((float) Math.pow(0.98, delta * 120));
        particle.position.mulAdd(particle.velocity, delta);
    }
}

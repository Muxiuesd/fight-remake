package ttk.muxiuesd.world.particle.motion;

import ttk.muxiuesd.world.particle.abs.Particle;

/**
 * 粒子应用空气阻力
 * */
public class PmcAirFriction implements ParticleMotionComp {
    @Override
    public void motion (Particle particle, float delta) {
        particle.velocity.scl((float) Math.pow(0.98, delta * 60));
        particle.position.mulAdd(particle.velocity, delta);
    }
}

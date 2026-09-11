package ttk.muxiuesd.world.particle.motion;

import ttk.muxiuesd.world.particle.abs.Particle;

/**
 * 粒子滚动旋转组件
 * <p>
 * 粒子的旋转角速度与当前运动速度挂钩（速度越快转得越快，类似滚动的效果）。
 * 速度随空气阻力衰减时，转速也随之减慢，滚动感自然
 * */
public class PmcRollRotate implements ParticleMotionComp {
    private float rotationSpeedFactor;   //每单位速度对应的角速度（度/秒/单位速度）

    public PmcRollRotate (float rotationSpeedFactor) {
        this.rotationSpeedFactor = rotationSpeedFactor;
    }

    @Override
    public void motion (Particle particle, float delta) {
        float speed = particle.velocity.len();   //当前速度大小
        //旋转角速度 ∝ 速度：速度越大转得越快
        particle.rotation += speed * this.rotationSpeedFactor * delta;
    }

    public float getRotationSpeedFactor () {
        return rotationSpeedFactor;
    }

    public PmcRollRotate setRotationSpeedFactor (float rotationSpeedFactor) {
        this.rotationSpeedFactor = rotationSpeedFactor;
        return this;
    }
}

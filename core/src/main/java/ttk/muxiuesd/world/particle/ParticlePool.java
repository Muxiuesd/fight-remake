package ttk.muxiuesd.world.particle;

import com.badlogic.gdx.utils.Pool;

/**
 * 粒子池
 * */
public class ParticlePool extends Pool<Particle> {

    public ParticlePool () {
        super(100);
    }

    @Override
    protected Particle newObject () {
        Particle particle = new Particle();
        //在这里初始化
        particle.init();
        return particle;
    }

    @Override
    protected void reset (Particle object) {
        super.reset(object);
    }
}

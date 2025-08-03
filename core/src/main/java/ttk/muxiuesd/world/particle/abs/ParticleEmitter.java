package ttk.muxiuesd.world.particle.abs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.particle.ParticlePool;
import ttk.muxiuesd.world.particle.motion.ParticleMotionComp;

/**
 * 粒子发射器
 * <p>
 * 粒子发射器主要管理同一种运动模式的同种粒子，同一种粒子但运动模式不同的话需要区分发射器
 * */
public abstract class ParticleEmitter<T extends Particle> implements Updateable, Drawable{
    private ParticlePool<T> particlePool;

    private Array<T> activeParticles;
    private Array<T> delayAddParticles;
    private Array<T> delayRemoveParticles;

    private Array<ParticleMotionComp> motionComps;

    public ParticleEmitter () {
        this.activeParticles = new Array<>();
        this.delayAddParticles = new Array<>();
        this.delayRemoveParticles = new Array<>();
        this.motionComps = new Array<>();
    }

    /**
     * 每个发射器所发射的粒子的运动逻辑
     * */
    public void motionLogic (T particle, float delta) {
        for (ParticleMotionComp motionComp : this.motionComps) {
            motionComp.motion(particle, delta);
        }
    }

    /**
     * 粒子的生成逻辑
     * */
    public abstract void summon (Vector2 position, Vector2 velocity, Vector2 origin,
                                 Vector2 startSize, Vector2 endSize, Vector2 scale,
                                 float rotation, float duration);

    @Override
    public void update (float delta) {
        if (this.delayRemoveParticles.size > 0) {
            this.activeParticles.removeAll(this.delayRemoveParticles, true);
            this.particlePool.freeAll(this.delayRemoveParticles);
            this.delayRemoveParticles.clear();
        }
        if (this.delayAddParticles.size > 0) {
            this.activeParticles.addAll(this.delayAddParticles);
            this.delayAddParticles.clear();
        }

        for (T particle : this.activeParticles) {
            if (particle.lifetime >= particle.duration) {
                this.removeParticle(particle);
                continue;
            }

            this.motionLogic(particle, delta);

            if (particle instanceof ShinyParticle shinyParticle) {
                shinyParticle.update(delta);
            }
            particle.lifetime += delta;
        }
    }

    @Override
    public void draw (Batch batch) {
        for (Particle p : this.activeParticles) {
            p.draw(batch);
        }
    }

    /**
     * 延迟增加粒子
     * */
    public void addParticle (T p) {
        this.delayAddParticles.add(p);
    }

    /**
     * 延迟移除粒子
     * */
    public void removeParticle (T p) {
        this.delayRemoveParticles.add(p);
    }

    /**
     * 添加粒子运动组件
     * */
    public void addMotionComp (ParticleMotionComp m) {
        if (this.motionComps.contains(m, true)) {
            Log.error(getClass().getName(), "粒子运动组件：" + m + " 已经存在于此粒子发射器，跳过添加！！！");
            return;
        }
        this.motionComps.add(m);
    }

    /**
     * 移除粒子运动组件
     * */
    public void removeMotionComp (ParticleMotionComp m) {
        if (!this.motionComps.contains(m, true)) {
            throw new IllegalArgumentException("这个粒子发射器不包含:" + m + " 粒子运动组件！！！");
        }
        this.motionComps.removeValue(m, true);
    }

    public ParticlePool<T> getParticlePool () {
        return this.particlePool;
    }

    public void setParticlePool (ParticlePool<T> particlePool) {
        this.particlePool = particlePool;
    }

    /**
     * 获取活跃的粒子数
     * */
    public int getActiveParticlesCount () {
        return this.activeParticles.size + this.delayAddParticles.size;
    }

    public Array<T> getActiveParticles () {
        return this.activeParticles;
    }
}

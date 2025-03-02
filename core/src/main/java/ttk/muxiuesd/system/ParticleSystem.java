package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.particle.Particle;
import ttk.muxiuesd.world.particle.ParticleAssets;
import ttk.muxiuesd.world.particle.ParticlePool;

/**
 * 粒子系统
 **/
public class ParticleSystem extends WorldSystem {
    public final String TAG = this.getClass().getName();

    private ParticlePool pool;
    private Array<Particle> activeParticles;
    private Array<Particle> delayAddParticles;
    private Array<Particle> delayRemoveParticles;

    public ParticleSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        ParticleAssets.loadAll();
        this.pool = new ParticlePool();
        this.activeParticles = new Array<>();
        this.delayAddParticles = new Array<>();
        this.delayRemoveParticles = new Array<>();
        Log.print(TAG, "粒子系统初始化完成");
    }

    @Override
    public void update (float delta) {
        if (this.delayRemoveParticles.size > 0) {
            this.activeParticles.removeAll(this.delayRemoveParticles, true);
            this.pool.freeAll(this.delayRemoveParticles);
            this.delayRemoveParticles.clear();
        }
        if (this.delayAddParticles.size > 0) {
            this.activeParticles.addAll(this.delayAddParticles);
            this.delayAddParticles.clear();
        }
        //进行更新
        for (Particle p : this.activeParticles) {
            if (p.lifetime >= p.duration) {
                this.removeParticle(p);
                continue;
            }
            // 空气阻力
            p.velocity.scl((float) Math.pow(0.98, delta * 60));
            p.position.mulAdd(p.velocity, delta);

            // 尺寸变化
            float t = p.lifetime / p.duration;
            p.curSize.x = p.startSize.x + (p.endSize.x - p.startSize.x) * t;
            p.curSize.y = p.startSize.y + (p.endSize.y - p.startSize.y) * t;

            p.lifetime += delta;
        }
    }

    @Override
    public void draw (Batch batch) {
        for (Particle p : this.activeParticles) {
            p.draw(batch);
        }
    }

    /**
     * 发射粒子
     * */
    public void emitParticle (String textureId,
                              Vector2 position, Vector2 velocity, Vector2 origin,
                              Vector2 startSize, Vector2 endSize, Vector2 scale,
                              float rotation, float duration) {
        Particle p = this.pool.obtain();
        p.region = new TextureRegion(AssetsLoader.getInstance().getById(textureId, Texture.class));
        p.position.set(position);
        p.velocity.set(velocity);
        p.origin.set(origin);
        p.startSize.set(startSize);
        p.endSize.set(endSize);
        p.scale.set(scale);
        p.rotation = rotation;
        p.duration = duration;
        //添加粒子
        this.addParticle(p);
    }

    /**
     * 延迟增加粒子
     * */
    public void addParticle (Particle p) {
        this.delayAddParticles.add(p);
    }

    /**
     * 延迟移除粒子
     * */
    public void removeParticle (Particle p) {
        this.delayRemoveParticles.add(p);
    }
}

package ttk.muxiuesd.world.particle.emitters;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.pool.particle.ParticleFirePool;
import ttk.muxiuesd.pool.particle.ParticlePool;
import ttk.muxiuesd.world.particle.ParticleFire;
import ttk.muxiuesd.world.particle.abs.ParticleEmitter;
import ttk.muxiuesd.world.particle.motion.PmcAirFriction;
import ttk.muxiuesd.world.particle.motion.PmcSizeTrans;

/**
 * 火焰粒子发射器
 * */
public class EmitterFurnaceFire extends ParticleEmitter<ParticleFire> {
    public static ParticlePool<ParticleFire> POOL = new ParticleFirePool();

    public EmitterFurnaceFire () {
        setParticlePool(POOL);
        addMotionComp(new PmcAirFriction());
        addMotionComp(new PmcSizeTrans());
    }

    @Override
    public void summon (Vector2 position, Vector2 velocity,
                        Vector2 origin, Vector2 startSize, Vector2 endSize,
                        Vector2 scale, float rotation, float duration) {
        ParticleFire p = getParticlePool().obtain();
        p.region = new TextureRegion(AssetsLoader.getInstance().getById(Fight.getId("fire"), Texture.class));
        p.position.set(position).add(MathUtils.random(-0.2f, 0.2f), 0);
        p.origin.set(origin);
        p.startSize.set(startSize).scl(MathUtils.random(0.5f, 1.1f));
        p.endSize.set(endSize);
        p.scale.set(scale);
        p.rotation = 0;
        p.duration = duration + MathUtils.random(-0.6f, 0.6f);

        // 初始化运动参数
        float angle = MathUtils.random(60, 120);
        float speed = MathUtils.random(velocity.len() * 0.3f, velocity.len() * 1.3f);
        p.velocity.set(0, speed).setAngleDeg(angle);

        //添加粒子
        addParticle(p);
    }

    @Override
    public void motionLogic (ParticleFire particle, float delta) {
        super.motionLogic(particle, delta);
        particle.origin.set(particle.curSize.x / 2, particle.curSize.y / 2);
    }
}

package ttk.muxiuesd.world.particle.emitters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.pool.particle.ParticleBubblePool;
import ttk.muxiuesd.pool.particle.ParticlePool;
import ttk.muxiuesd.world.particle.ParticleBubble;
import ttk.muxiuesd.world.particle.abs.ParticleEmitter;
import ttk.muxiuesd.world.particle.motion.PmcSizeTrans;
import ttk.muxiuesd.world.particle.motion.PmcWaterFriction;

/**
 * 实体在水中游泳粒子发射器
 * */
public class EmitterEntitySwimming extends ParticleEmitter<ParticleBubble> {
    public static ParticlePool<ParticleBubble> POOL = new ParticleBubblePool();
    public EmitterEntitySwimming () {
        setParticlePool(POOL);
        addMotionComp(new PmcWaterFriction());
        addMotionComp(new PmcSizeTrans());
    }

    @Override
    public void motionLogic (ParticleBubble particle, float delta) {
        super.motionLogic(particle, delta);
        particle.origin.set(particle.curSize.x / 2, particle.curSize.y / 2);
    }

    @Override
    public void summon (Vector2 position, Vector2 velocity,
                        Vector2 origin, Vector2 startSize, Vector2 endSize,
                        Vector2 scale,
                        float rotation, float duration) {
        ParticleBubble p = getParticlePool().obtain();
        p.region = new TextureRegion(AssetsLoader.getInstance().getById(Fight.getId("bubble"), Texture.class));
        p.position.set(position.x + MathUtils.random(-0.5f, 0.5f),
            position.y + MathUtils.random(-0.1f, 0.1f));
        p.origin.set(origin);
        p.startSize.set(startSize);
        p.endSize.set(endSize);
        p.scale.set(scale);
        p.rotation = rotation + MathUtils.random(0, 360);
        p.duration = duration + MathUtils.random(-0.4f, 1f);

        p.velocity.set(velocity).setAngleDeg(MathUtils.random(0, 360));

        addParticle(p);
    }
}

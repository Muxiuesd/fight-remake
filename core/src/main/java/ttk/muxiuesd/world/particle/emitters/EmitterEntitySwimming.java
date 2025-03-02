package ttk.muxiuesd.world.particle.emitters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.world.particle.ParticleBubble;
import ttk.muxiuesd.world.particle.ParticlePool;
import ttk.muxiuesd.world.particle.abs.ParticleEmitter;

/**
 * 实体在水中游泳粒子发射器
 * */
public class EmitterEntitySwimming extends ParticleEmitter<ParticleBubble> {
    public EmitterEntitySwimming () {
        setParticlePool(new ParticlePool<>(100) {
            @Override
            protected ParticleBubble newObject () {
                ParticleBubble particle = new ParticleBubble();
                particle.init();
                return particle;
            }
        });
    }

    @Override
    public void motionLogic (ParticleBubble particle, float delta) {
        //水体阻力
        particle.velocity.scl((float) Math.pow(0.98, delta * 60));
        particle.position.mulAdd(particle.velocity, delta);

        // 尺寸变化
        float t = particle.lifetime / particle.duration;
        particle.curSize.x = particle.startSize.x + (particle.endSize.x - particle.startSize.x) * t;
        particle.curSize.y = particle.startSize.y + (particle.endSize.y - particle.startSize.y) * t;

        particle.lifetime += delta;
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
        p.rotation = rotation;
        p.duration = duration;

        p.velocity.set(velocity).setAngleDeg(MathUtils.random(0, 360));

        addParticle(p);
    }
}

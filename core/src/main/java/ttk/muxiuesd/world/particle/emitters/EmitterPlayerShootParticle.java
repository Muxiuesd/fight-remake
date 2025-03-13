package ttk.muxiuesd.world.particle.emitters;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.assetsloader.AssetsLoader;
import ttk.muxiuesd.world.particle.ParticlePool;
import ttk.muxiuesd.world.particle.ParticleSpell;
import ttk.muxiuesd.world.particle.abs.ParticleEmitter;

/**
 * 玩家发射子弹的粒子发射器
 * */
public class EmitterPlayerShootParticle extends ParticleEmitter<ParticleSpell> {
    public EmitterPlayerShootParticle () {
        setParticlePool(new ParticlePool<>(100) {
            @Override
            protected ParticleSpell newObject () {
                ParticleSpell particleSpell = new ParticleSpell();
                particleSpell.init();
                return particleSpell;
            }
        });
        //这里设置默认的贴图会出错
        //this.region = new TextureRegion(AssetsLoader.getInstance().getById(Fight.getId("spell"), Texture.class));
    }

    @Override
    public void motionLogic (ParticleSpell particle, float delta) {
        // 空气阻力
        particle.velocity.scl((float) Math.pow(0.98, delta * 60));
        particle.position.mulAdd(particle.velocity, delta);

        // 尺寸变化
        float t = particle.lifetime / particle.duration;
        particle.curSize.x = particle.startSize.x + (particle.endSize.x - particle.startSize.x) * t;
        particle.curSize.y = particle.startSize.y + (particle.endSize.y - particle.startSize.y) * t;

        particle.origin.set(particle.curSize.x / 2, particle.curSize.y / 2);
        //particle.rotation += (particle.duration - particle.lifetime) * 360;

        particle.lifetime += delta;
    }

    @Override
    public void summon (Vector2 position, Vector2 velocity, Vector2 origin,
                        Vector2 startSize, Vector2 endSize, Vector2 scale,
                        float rotation, float duration) {
        ParticleSpell p = getParticlePool().obtain();
        p.region = new TextureRegion(AssetsLoader.getInstance().getById(Fight.getId("spell"), Texture.class));
        p.position.set(position);
        p.origin.set(origin);
        p.startSize.set(startSize).scl(MathUtils.random(0.8f, 1.3f));
        p.endSize.set(endSize);
        p.scale.set(scale);
        p.rotation = rotation + MathUtils.random(0, 360);
        p.duration = duration + MathUtils.random(-0.7f, 0.5f);

        // 初始化运动参数
        float angle = MathUtils.random(0, 360);
        float speed = MathUtils.random(velocity.len() * 0.3f, velocity.len() * 1.3f);
        p.velocity.set(speed, speed).setAngleDeg(angle);

        //添加粒子
        addParticle(p);
    }

    @Override
    public void draw (Batch batch) {
        //ShaderProgram particleShader = ShaderScheduler.getInstance().begin(ShaderReg.PARTICLE_2_SHADER, batch);
        //particleShader.setUniformMatrix("u_projTrans", batch.getProjectionMatrix());
        //super.draw(batch);
        for (ParticleSpell p : getActiveParticles()) {
            Color color = p.light.getColor();
            batch.setColor(color.r, color.g, color.b, 1f);
            p.draw(batch);
            batch.setColor(1, 1, 1, 1);
        }
    }
}

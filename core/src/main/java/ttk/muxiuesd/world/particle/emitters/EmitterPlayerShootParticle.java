package ttk.muxiuesd.world.particle.emitters;

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
import ttk.muxiuesd.world.particle.motion.PmcAirFriction;
import ttk.muxiuesd.world.particle.motion.PmcSizeTrans;

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
        addMotionComp(new PmcAirFriction());
        addMotionComp(new PmcSizeTrans());
    }

    @Override
    public void motionLogic (ParticleSpell particle, float delta) {
        super.motionLogic(particle, delta);

        particle.origin.set(particle.curSize.x / 2, particle.curSize.y / 2);
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
        super.draw(batch);
    }
}

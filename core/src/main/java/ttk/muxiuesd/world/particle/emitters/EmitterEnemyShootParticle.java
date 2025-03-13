package ttk.muxiuesd.world.particle.emitters;

import com.badlogic.gdx.graphics.Texture;
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
 * 敌人射出子弹的粒子发射器
 * */
public class EmitterEnemyShootParticle extends ParticleEmitter<ParticleSpell> {
    public EmitterEnemyShootParticle () {
        setParticlePool(new ParticlePool<>(100) {
            @Override
            protected ParticleSpell newObject () {
                ParticleSpell spell = new ParticleSpell();
                spell.init();
                return spell;
            }
        });
        addMotionComp(new PmcAirFriction());
        addMotionComp(new PmcSizeTrans());
    }

    @Override
    public void motionLogic (ParticleSpell particle, float delta) {// 空气阻力
        super.motionLogic(particle, delta);
        particle.origin.set(particle.curSize.x / 2, particle.curSize.y / 2);
    }

    @Override
    public void summon (Vector2 position, Vector2 velocity, Vector2 origin, Vector2 startSize, Vector2 endSize, Vector2 scale, float rotation, float duration) {
        ParticleSpell p = getParticlePool().obtain();
        p.region = new TextureRegion(AssetsLoader.getInstance().getById(Fight.getId("spell"), Texture.class));
        p.position.set(position);
        p.origin.set(origin);
        p.startSize.set(startSize);
        p.endSize.set(endSize);
        p.scale.set(scale);
        p.rotation = rotation + MathUtils.random(0, 360);
        p.duration = duration + MathUtils.random(-0.3f, 1f);

        // 初始化运动参数
        float angle = MathUtils.random(0, 360);
        float speed = MathUtils.random(velocity.len() * 0.2f, velocity.len() * 1.2f);
        p.velocity.set(speed, speed).setAngleDeg(angle);

        //添加粒子
        addParticle(p);
    }
}

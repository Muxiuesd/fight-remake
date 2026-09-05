package ttk.muxiuesd.world.particle.emitters;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.pool.particle.ParticleBreakPool;
import ttk.muxiuesd.pool.particle.ParticlePool;
import ttk.muxiuesd.world.particle.BlockBreakParticle;
import ttk.muxiuesd.world.particle.abs.ParticleEmitter;
import ttk.muxiuesd.world.particle.motion.PmcAirFriction;
import ttk.muxiuesd.world.particle.motion.PmcSizeTrans;

/**
 * 方块破坏残渣粒子发射器
 * <p>
 * 贴图由 {@link #setSummonRegion(TextureRegion)} 传入方块贴图，
 * 每个粒子随机裁剪方块贴图的一小块作为残渣碎片；无重力，随机向任意方向散开后减速缩小
 * */
public class EmitterBlockBreak extends ParticleEmitter<BlockBreakParticle> {
    public static ParticlePool<BlockBreakParticle> POOL = new ParticleBreakPool();

    public EmitterBlockBreak () {
        setParticlePool(POOL);
        addMotionComp(new PmcAirFriction());
        addMotionComp(new PmcSizeTrans());
    }

    @Override
    public void summon (Vector2 position, Vector2 velocity, Vector2 origin,
                        Vector2 startSize, Vector2 endSize,
                        Vector2 scale, float rotation, float duration) {

        TextureRegion summonRegion = getSummonRegion();
        TextureRegion baseTextureRegion = summonRegion != null ? summonRegion : getTextureRegion();

        //没有贴图就返回
        if (baseTextureRegion == null) return;

        //贴图：从本次召唤贴图（方块贴图）随机裁剪一小块作为残渣碎片
        BlockBreakParticle p = getParticlePool().obtain();
        float u  = MathUtils.lerp(baseTextureRegion.getU(),  baseTextureRegion.getU2(), MathUtils.random());
        float v  = MathUtils.lerp(baseTextureRegion.getV(),  baseTextureRegion.getV2(), MathUtils.random());
        float u2 = MathUtils.lerp(u, baseTextureRegion.getU2(), MathUtils.random(0.15f, 0.5f));
        float v2 = MathUtils.lerp(v, baseTextureRegion.getV2(), MathUtils.random(0.15f, 0.5f));
        p.region = new TextureRegion(baseTextureRegion.getTexture(), u, v, u2, v2);

        p.position.set(position).add(MathUtils.random(-0.25f, 0.25f), MathUtils.random(-0.25f, 0.25f));
        p.origin.set(origin);
        p.startSize.set(startSize).scl(MathUtils.random(0.6f, 1.2f));
        p.endSize.set(endSize);
        p.scale.set(scale);
        p.rotation = MathUtils.random(0, 360);
        p.duration = duration + MathUtils.random(-0.15f, 0.25f);

        //速度：随机向任意方向散开（0~360°），力度随机
        float angle = MathUtils.random(0, 360);
        float speed = MathUtils.random(0.5f, 2f);
        double xv = speed * Math.cos(angle);
        double yv = speed * Math.sin(angle);
        p.velocity.set((float) xv, (float) yv).setAngleDeg(angle);

        addParticle(p);
    }
}

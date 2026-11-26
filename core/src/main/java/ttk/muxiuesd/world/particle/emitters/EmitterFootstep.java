package ttk.muxiuesd.world.particle.emitters;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.pool.particle.ParticleFootstepPool;
import ttk.muxiuesd.pool.particle.ParticlePool;
import ttk.muxiuesd.world.particle.ParticleFootstep;
import ttk.muxiuesd.world.particle.abs.ParticleEmitter;
import ttk.muxiuesd.world.particle.motion.PmcAirFriction;
import ttk.muxiuesd.world.particle.motion.PmcRollRotate;
import ttk.muxiuesd.world.particle.motion.PmcSizeTrans;

/**
 * 玩家行走脚下粒子发射器
 * <p>
 * 贴图由 {@link #setSummonRegion(TextureRegion)} 传入脚下方块贴图，
 * 每个粒子随机裁剪方块贴图的一小块作为碎片；只受空气阻力，绕中心滚动旋转，
 * 尺寸由大到小渐变
 * */
public class EmitterFootstep extends ParticleEmitter<ParticleFootstep> {
    public static ParticlePool<ParticleFootstep> POOL = new ParticleFootstepPool();

    public EmitterFootstep () {
        setParticlePool(POOL);
        addMotionComp(new PmcAirFriction());    //只受空气阻力
        addMotionComp(new PmcSizeTrans());      //尺寸从大到小渐变
        addMotionComp(new PmcRollRotate(160f)); //滚动旋转（每单位速度 160°/s）
    }

    @Override
    public void summon (Vector2 position, Vector2 velocity, Vector2 origin,
                        Vector2 startSize, Vector2 endSize,
                        Vector2 scale, float rotation, float duration) {

        TextureRegion summonRegion = getSummonRegion();
        TextureRegion baseTextureRegion = summonRegion != null ? summonRegion : getTextureRegion();

        //没有贴图就返回
        if (baseTextureRegion == null) return;

        //贴图：从本次召唤贴图（脚下方块贴图）随机裁剪一小块作为碎片
        ParticleFootstep p = getParticlePool().obtain();
        float u  = MathUtils.lerp(baseTextureRegion.getU(),  baseTextureRegion.getU2(), MathUtils.random());
        float v  = MathUtils.lerp(baseTextureRegion.getV(),  baseTextureRegion.getV2(), MathUtils.random());
        float u2 = MathUtils.lerp(u, baseTextureRegion.getU2(), MathUtils.random(0.15f, 0.5f));
        float v2 = MathUtils.lerp(v, baseTextureRegion.getV2(), MathUtils.random(0.15f, 0.5f));
        p.region = new TextureRegion(baseTextureRegion.getTexture(), u, v, u2, v2);

        //生成坐标：相对传入的脚底位置随机偏移一小段距离（碎片散落在脚边，而不是全部叠在一点）
        p.position.set(position).add(MathUtils.random(-0.3f, 0.3f), MathUtils.random(-0.15f, 0.1f));
        p.origin.set(origin);   //粒子中心：滚动绕中心旋转
        p.startSize.set(startSize);
        p.endSize.set(endSize);   //由 PmcSizeTrans 从大到小渐变
        p.scale.set(scale);
        p.rotation = MathUtils.random(0, 360);   //初始随机角度
        p.duration = duration + MathUtils.random(-0.1f, 0.15f);

        //速度：向行走方向的反方向发出，放慢幅度，并随机偏离一个小角度（碎片不完全沿正后方）
        p.velocity.set(velocity);
        float angle = p.velocity.angleDeg() + MathUtils.random(-25f, 25f);   //随机偏离 ±25°
        p.velocity.setAngleDeg(angle).scl(MathUtils.random(0.03f, 0.07f));   //放慢：仅为反向速度的一小部分

        addParticle(p);
    }
}

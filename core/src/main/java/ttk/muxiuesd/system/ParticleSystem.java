package ttk.muxiuesd.system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.interfaces.render.IWorldParticleRender;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.particle.ParticleAssets;
import ttk.muxiuesd.world.particle.ParticleDefaultConfig;
import ttk.muxiuesd.world.particle.ParticleEmittersReg;
import ttk.muxiuesd.world.particle.abs.Particle;
import ttk.muxiuesd.world.particle.abs.ParticleEmitter;

/**
 * 粒子系统
 **/
public class ParticleSystem extends WorldSystem implements IWorldParticleRender {
    public final String TAG = this.getClass().getName();

    private Array<ParticleEmitter<? extends Particle>> activeEmitters;  //活跃的粒子发射器
    private Array<ParticleEmitter<? extends Particle>> delayAddEmitters;
    private Array<ParticleEmitter<? extends Particle>> delayRemoveEmitters;


    public ParticleSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        ParticleAssets.init();
        ParticleEmittersReg.init();

        this.activeEmitters = new Array<>();
        this.delayAddEmitters = new Array<>();
        this.delayRemoveEmitters = new Array<>();

        Log.print(TAG(), "粒子系统初始化完成");
    }

    @Override
    public void update (float delta) {
        if (this.delayAddEmitters.size > 0) {
            this.activeEmitters.addAll(this.delayAddEmitters);
            this.delayAddEmitters.clear();
        }
        if (this.delayRemoveEmitters.size > 0) {
            this.activeEmitters.removeAll(this.delayRemoveEmitters, true);
            this.delayRemoveEmitters.clear();
        }

        for (ParticleEmitter<? extends Particle> emitter : this.activeEmitters) {
            //把没有活跃粒子的粒子发射器移出来，跳过更新
            if (emitter.getActiveParticlesCount() <= 0) {
                this.delayRemoveEmitters.add(emitter);
                continue;
            }
            emitter.update(delta);
        }
    }

    @Override
    public void draw (Batch batch) {
        // 设置混合模式
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
        for (ParticleEmitter<?> emitter : this.activeEmitters) {
            emitter.draw(batch);
            LightSystem lightSystem = getManager().getSystem(LightSystem.class);
            lightSystem.useLight(emitter.getActiveParticles());
        }
        // 恢复默认混合模式
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void render (Batch batch, ShapeRenderer shapeRenderer) {
        this.draw(batch);
    }

    @Override
    public int getRenderPriority () {
        return 100;
    }

    /**
     * 简单发射粒子，使用默认参数
     * TODO 用id来使用不同的默认参数
     * */
    public void emitParticle (String emitterId, int count, Vector2 position, ParticleDefaultConfig defaultConfig) {
        this.emitParticle(emitterId, count,
            position, defaultConfig.velocity, defaultConfig.origin,
            defaultConfig.startSize, defaultConfig.endSize, defaultConfig.scale,
            defaultConfig.rotation, defaultConfig.duration);
    }

    public void emitParticle (String emitterId, int count,
                              Vector2 position, Vector2 velocity,
                              float duration, ParticleDefaultConfig defaultConfig) {
        this.emitParticle(emitterId, count,
            position, velocity, new Vector2(),
            defaultConfig.startSize, defaultConfig.endSize, defaultConfig.scale,
            0f, duration);
    }

    /**
     * 发射粒子
     * <p>
     * 传入的参数都是最初始的参数，会影响后续运动逻辑
     * */
    public void emitParticle (String emitterId, int count,
                              Vector2 position, Vector2 velocity, Vector2 origin,
                              Vector2 startSize, Vector2 endSize, Vector2 scale,
                              float rotation, float duration) {
        ParticleEmitter<? extends Particle> emitter = this.activateEmitter(emitterId);
        for (int i = 0; i < count; i++) {
            emitter.summon(position, velocity, origin, startSize, endSize, scale, rotation, duration);
        }
    }
    /**
     * 激活粒子发射器
     * @return 返回激活的粒子发射器
     * */
    private ParticleEmitter<? extends Particle> activateEmitter (String id) {
        ParticleEmitter<? extends Particle> emitter = ParticleEmittersReg.get(id);
        if (this.activeEmitters.contains(emitter, true)) {
            //Log.error(TAG, "id为：" + id + " 的粒子发射器已经活跃！！！");
            return emitter;
        }
        this.delayAddEmitters.add(emitter);
        return emitter;
    }


}

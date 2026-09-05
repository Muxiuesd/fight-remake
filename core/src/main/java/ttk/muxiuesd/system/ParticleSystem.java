package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.interfaces.render.IWorldParticleRender;
import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.registrant.BlockRendererRegistry;
import ttk.muxiuesd.registry.ParticleEmitters;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.particle.BlockBreakParticle;
import ttk.muxiuesd.world.particle.ParticleDefaultConfig;
import ttk.muxiuesd.world.particle.abs.Particle;
import ttk.muxiuesd.world.particle.abs.ParticleEmitter;

/**
 * 粒子系统
 **/
public class ParticleSystem extends WorldSystem implements IWorldParticleRender {
    public final String TAG = this.getClass().getName();

    private Array<ParticleEmitter<? extends Particle>> activeEmitters;      //活跃的粒子发射器
    private Array<ParticleEmitter<? extends Particle>> delayAddEmitters;    //延迟添加队列
    private Array<ParticleEmitter<? extends Particle>> delayRemoveEmitters; //延迟移除队列


    public ParticleSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        ParticleEmitters.init();

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
        for (ParticleEmitter<?> emitter : this.activeEmitters) {
            emitter.draw(batch);
            LightSystem lightSystem = getManager().getSystem(LightSystem.class);
            lightSystem.useLight(emitter.getActiveParticles());
        }
    }

    @Override
    public void batchRender (Batch batch) {
        this.draw(batch);
    }

    @Override
    public void shapeRender (ShapeRenderer shapeRenderer) {
    }

    @Override
    public int getRenderPriority () {
        return 100;
    }

    /**
     * 简单发射粒子，使用默认参数
     * TODO 用id来使用不同的默认参数
     * */
    public void emitParticle (ParticleEmitter<?> emitter, int count, Vector2 position, ParticleDefaultConfig defaultConfig) {
        this.emitParticle(emitter, count,
            position, defaultConfig.velocity, defaultConfig.origin,
            defaultConfig.startSize, defaultConfig.endSize, defaultConfig.scale,
            defaultConfig.rotation, defaultConfig.duration);
    }

    public void emitParticle (ParticleEmitter<?> emitter, int count,
                              Vector2 position, Vector2 velocity,
                              float duration, ParticleDefaultConfig defaultConfig) {
        this.emitParticle(emitter, count,
            position, velocity, new Vector2(),
            defaultConfig.startSize, defaultConfig.endSize, defaultConfig.scale,
            0f, duration);
    }

    /**
     * 发射粒子（直接传入粒子发射器实例，不走注册表查询）
     * <p>
     * 传入的参数都是最初始的参数，会影响后续运动逻辑
     * */
    public void emitParticle (ParticleEmitter<?> emitter, int count,
                              Vector2 position, Vector2 velocity, Vector2 origin,
                              Vector2 startSize, Vector2 endSize, Vector2 scale,
                              float rotation, float duration) {
        this.activateEmitter(emitter);
        for (int i = 0; i < count; i++) {
            emitter.summon(position, velocity, origin, startSize, endSize, scale, rotation, duration);
        }
    }

    /**
     * 激活粒子发射器
     * @param emitter 粒子发射器实例
     */
    private void activateEmitter (ParticleEmitter<?> emitter) {
        //已经活跃就跳过
        if (this.activeEmitters.contains(emitter, true)) return;

        //如果在延迟移除队列里面就从中移除
        if (this.delayRemoveEmitters.contains(emitter, true)) {
            this.delayRemoveEmitters.removeValue(emitter, true);
        }
        //添加进延迟添加队列
        this.delayAddEmitters.add(emitter);
    }

    /**
     * 发射方块破坏残渣粒子
     * <p>
     * 根据方块贴图生成若干残渣粒子，随机向任意方向散开（无重力）
     * @param block 被破坏的方块（取其渲染器贴图作为残渣贴图）
     * @param position 破坏位置（方块中心）
     */
    public void blockBreakParticle (Block block, Vector2 position) {
        BlockRenderer<? extends Block> renderer = BlockRendererRegistry.getOrNull(block);
        if (renderer == null) return;
        TextureRegion region = renderer.getTextureRegion();
        if (region == null) return;

        ParticleEmitter<BlockBreakParticle> emitter = ParticleEmitters.BLOCK_BREAK;
        emitter.setSummonRegion(region);
        this.activateEmitter(emitter);
        int count = MathUtils.random(6, 12);
        for (int i = 0; i < count; i++) {
            emitter.summon(position, Vector2.Zero, Vector2.Zero,
                new Vector2(0.2f, 0.2f), new Vector2(0.02f, 0.02f),
                Vector2.One,
                MathUtils.random(0, 360), MathUtils.random(0.4f, 0.7f));
        }
        emitter.setSummonRegion(null);   //用完复位（发射器是共享单例）
    }


    public Array<ParticleEmitter<? extends Particle>> getActiveEmitters () {
        return this.activeEmitters;
    }

    public ParticleSystem setActiveEmitters (Array<ParticleEmitter<? extends Particle>> activeEmitters) {
        this.activeEmitters = activeEmitters;
        return this;
    }

    public Array<ParticleEmitter<? extends Particle>> getDelayAddEmitters () {
        return this.delayAddEmitters;
    }

    public ParticleSystem setDelayAddEmitters (Array<ParticleEmitter<? extends Particle>> delayAddEmitters) {
        this.delayAddEmitters = delayAddEmitters;
        return this;
    }

    public Array<ParticleEmitter<? extends Particle>> getDelayRemoveEmitters () {
        return this.delayRemoveEmitters;
    }

    public ParticleSystem setDelayRemoveEmitters (Array<ParticleEmitter<? extends Particle>> delayRemoveEmitters) {
        this.delayRemoveEmitters = delayRemoveEmitters;
        return this;
    }
}

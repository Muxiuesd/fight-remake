package ttk.muxiuesd.system;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.abs.WorldSystem;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.particle.ParticleAssets;
import ttk.muxiuesd.world.particle.abs.ParticleEmitter;
import ttk.muxiuesd.world.particle.emitters.EmitterPlayerBulletParticle;

import java.util.LinkedHashMap;

/**
 * 粒子系统
 **/
public class ParticleSystem extends WorldSystem {
    public final String TAG = this.getClass().getName();

    private LinkedHashMap<String, ParticleEmitter> emitters;

    private Array<ParticleEmitter> activeEmitters;  //活跃的粒子发射器
    private Array<ParticleEmitter> delayAddEmitters;
    private Array<ParticleEmitter> delayRemoveEmitters;


    public ParticleSystem (World world) {
        super(world);
    }

    @Override
    public void initialize () {
        ParticleAssets.loadAll();
        this.emitters = new LinkedHashMap<>();
        this.activeEmitters = new Array<>();
        this.delayAddEmitters = new Array<>();
        this.delayRemoveEmitters = new Array<>();

        this.addEmitter(Fight.getId("player_shoot"), new EmitterPlayerBulletParticle());

        Log.print(TAG, "粒子系统初始化完成");
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

        for (ParticleEmitter emitter : this.activeEmitters) {
            //把没有活跃粒子的粒子发射器移出来，跳过更新
            if (emitter.getActiveParticlesCount() <= 0) {
                this.delayRemoveEmitters.add(emitter);
                continue;
            }
            emitter.update(delta);
        }
        //System.out.println(this.activeEmitters.size);
    }

    @Override
    public void draw (Batch batch) {
        for (ParticleEmitter emitter : this.activeEmitters) {
            emitter.draw(batch);
        }
    }

    /**
     * 发射粒子
     * */
    public void emitParticle (String emitterId, int count,
                              Vector2 position, Vector2 velocity, Vector2 origin,
                              Vector2 startSize, Vector2 endSize, Vector2 scale,
                              float rotation, float duration) {
        if (!this.emitters.containsKey(emitterId)) {
            throw new IllegalArgumentException("id为：" + emitterId + " 的粒子发射器不存在！！！");
        }
        ParticleEmitter emitter = this.activateEmitter(emitterId);
        for (int i = 0; i < count; i++) {
            emitter.summon(position, velocity, origin, startSize, endSize, scale, rotation, duration);
        }
    }

    /**
     * 添加一种粒子发射器
     * */
    public void addEmitter (String id, ParticleEmitter emitter) {
        if (this.emitters.containsKey(id)) {
            throw new RuntimeException("发射器id：" + id + " 已存在，不可重复添加！！！");
        }
        this.emitters.put(id, emitter);
    }

    /**
     * 激活粒子发射器
     * @return 返回激活的粒子发射器
     * */
    private ParticleEmitter activateEmitter (String id) {
        ParticleEmitter emitter = this.emitters.get(id);
        if (this.activeEmitters.contains(emitter, true)) {
            Log.error(TAG, "id为：" + id + " 的粒子发射器已经活跃！！！");
            return emitter;
        }
        this.delayAddEmitters.add(emitter);
        return emitter;
    }
}

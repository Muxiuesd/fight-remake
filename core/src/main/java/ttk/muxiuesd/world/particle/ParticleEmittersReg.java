package ttk.muxiuesd.world.particle;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.particle.abs.Particle;
import ttk.muxiuesd.world.particle.abs.ParticleEmitter;
import ttk.muxiuesd.world.particle.emitters.EmitterEnemyShootParticle;
import ttk.muxiuesd.world.particle.emitters.EmitterEntitySwimming;
import ttk.muxiuesd.world.particle.emitters.EmitterFurnaceFire;
import ttk.muxiuesd.world.particle.emitters.EmitterPlayerShootParticle;

import java.util.LinkedHashMap;

/**
 * 粒子发射器注册
 * */
public class ParticleEmittersReg {
    private static final LinkedHashMap<String, ParticleEmitter<? extends Particle>> emitters = new LinkedHashMap<>();

    public static final String FURNACE_FIRE = register(Fight.getId("furnace_fire"), new EmitterFurnaceFire());
    public static final String PLAYER_SHOOT = register(Fight.getId("player_shoot"), new EmitterPlayerShootParticle());
    public static final String ENTITY_SWIMMING = register(Fight.getId("entity_swimming"), new EmitterEntitySwimming());
    public static final String ENTITY_SHOOT = register(Fight.getId("enemy_shoot"), new EmitterEnemyShootParticle());

    /**
     * 添加一种粒子发射器
     * */
    public static String register (String id, ParticleEmitter<? extends Particle> emitter) {
        if (contains(id)) {
            throw new RuntimeException("发射器id：" + id + " 已存在，不可重复添加！！！");
        }
        emitters.put(id, emitter);
        return id;
    }

    public static ParticleEmitter<? extends Particle> get (String id) {
        if (! contains(id)) {
            throw new IllegalArgumentException("没有id为：" + id + " 的粒子发射器！！！");
        }
        return emitters.get(id);
    }

    public static boolean contains (String id) {
        return emitters.containsKey(id);
    }
}

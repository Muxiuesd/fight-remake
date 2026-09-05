package ttk.muxiuesd.registry;

import game.muxiuesd.bedrockcore.util.Log;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.particle.BlockBreakParticle;
import ttk.muxiuesd.world.particle.ParticleBubble;
import ttk.muxiuesd.world.particle.ParticleFire;
import ttk.muxiuesd.world.particle.ParticleSpell;
import ttk.muxiuesd.world.particle.abs.Particle;
import ttk.muxiuesd.world.particle.abs.ParticleEmitter;
import ttk.muxiuesd.world.particle.emitters.EmitterBlockBreak;
import ttk.muxiuesd.world.particle.emitters.EmitterEnemyShootParticle;
import ttk.muxiuesd.world.particle.emitters.EmitterEntitySwimming;
import ttk.muxiuesd.world.particle.emitters.EmitterFurnaceFire;
import ttk.muxiuesd.world.particle.emitters.EmitterPlayerShootParticle;

/**
 * 粒子发射器的注册表
 * */
public class ParticleEmitters {
    public static void init () {
        Log.print(ParticleEmitters.class.getName(), "粒子发射器注册完毕");
    }

    public static final ParticleEmitter<ParticleFire> FURNACE_FIRE = register("furnace_fire", new EmitterFurnaceFire());
    public static final ParticleEmitter<ParticleSpell> PLAYER_SHOOT = register("player_shoot", new EmitterPlayerShootParticle());
    public static final ParticleEmitter<ParticleBubble> ENTITY_SWIMMING = register("entity_swimming", new EmitterEntitySwimming());
    public static final ParticleEmitter<ParticleSpell> ENTITY_SHOOT = register("enemy_shoot", new EmitterEnemyShootParticle());
    public static final ParticleEmitter<BlockBreakParticle> BLOCK_BREAK = register("block_break", new EmitterBlockBreak());


    public static <T extends Particle> ParticleEmitter<T> register (String name, ParticleEmitter<T> emitter) {
        return register(Identifier.of(Fight.ID(name)), emitter);
    }

    /**
     * 最基础的粒子发射器注册
     * */
    public static <T extends Particle> ParticleEmitter<T> register (Identifier identifier, ParticleEmitter<T> emitter) {
        Registries.PARTICLE_EMITTER.register(identifier, emitter);
        return emitter;
    }
}

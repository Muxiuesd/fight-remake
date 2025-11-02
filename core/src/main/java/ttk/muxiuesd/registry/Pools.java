package ttk.muxiuesd.registry;

import com.badlogic.gdx.utils.Pool;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.pool.FightPool;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.util.Timer;
import ttk.muxiuesd.util.pool.PoolableRectangle;
import ttk.muxiuesd.util.pool.PoolableVec2;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.genfactory.ItemEntityGetter;
import ttk.muxiuesd.world.particle.ParticleBubble;
import ttk.muxiuesd.world.particle.ParticleFire;
import ttk.muxiuesd.world.particle.ParticleSpell;
import ttk.muxiuesd.world.particle.emitters.EmitterEnemyShootParticle;
import ttk.muxiuesd.world.particle.emitters.EmitterEntitySwimming;
import ttk.muxiuesd.world.particle.emitters.EmitterFurnaceFire;

/**
 * 游戏内的注册的对象池
 * */
public final class Pools {
    public static void init() {
        Log.print(Pools.class.getName(), "游戏内所有对象池注册完毕");
    }

    /// 工具类池
    public static final FightPool<PoolableVec2> VEC2 = register("vec2", PoolableVec2.class);
    public static final FightPool<PoolableRectangle> RECT = register("rectangle", PoolableRectangle.class);
    public static final FightPool<Timer> TIMER = register("timer", Timer.class);
    public static final FightPool<TaskTimer> TASK_TIMER = register("timer", TaskTimer.class);

    /// 粒子池
    public static final FightPool<ParticleSpell> SPELL = register("spell", ParticleSpell.class, EmitterEnemyShootParticle.POOL);
    public static final FightPool<ParticleBubble> BUBBLE = register("bubble", ParticleBubble.class, EmitterEntitySwimming.POOL);
    public static final FightPool<ParticleFire> FIRE = register("fire", ParticleFire.class, EmitterFurnaceFire.POOL);

    /// 实体池
    public static final FightPool<ItemEntity> ITEM_ENTITY = register("item_entity", ItemEntity.class, ItemEntityGetter.POOL);



    public static <T> FightPool<T> register (String name, Class<T> type) {
        FightPool<T> fightPool = new FightPool<>(type);
        return register(name, fightPool);
    }

    public static <T, P extends Pool<T>> FightPool<T> register (String name, Class<T> type, P pool) {
        FightPool<T> fightPool = new FightPool<>(type, pool);
        return register(name, fightPool);
    }

    public static <T> FightPool<T> register (String name, FightPool<T> fightPool) {
        return (FightPool<T>) Registries.POOL.register(new Identifier(Fight.ID(name)), fightPool);
    }

}

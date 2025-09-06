package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.pool.FightPool;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.util.Timer;
import ttk.muxiuesd.util.pool.PoolableRectangle;
import ttk.muxiuesd.util.pool.PoolableVec2;

/**
 * 游戏内的注册的对象池
 * */
public final class Pools {
    public static void init() {
        Log.print(Pools.class.getName(), "游戏内所有对象池注册完毕");
    }

    public static final FightPool<PoolableVec2> VEC2 = register("vec2", PoolableVec2.class);
    public static final FightPool<PoolableRectangle> RECT = register("rectangle", PoolableRectangle.class);
    public static final FightPool<Timer> TIMER = register("timer", Timer.class);
    public static final FightPool<TaskTimer> TASK_TIMER = register("timer", TaskTimer.class);

    public static <T> FightPool<T> register (String name, Class<T> type) {
        FightPool<T> pool = new FightPool<>(type);
        return (FightPool<T>) Registries.POOL.register(new Identifier(Fight.getId(name)), pool);
    }
}

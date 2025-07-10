package ttk.muxiuesd.registry;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.pool.FightPool;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.util.Log;

/**
 * 游戏内的注册的对象池
 * */
public final class Pools {
    public static void init() {
        Log.print(Pools.class.getName(), "游戏内所有对象池注册完毕");
    }

    public static final FightPool<Vector2> VEC2 = register("vec2", Vector2.class);


    public static <T> FightPool<T> register (String name, Class<T> type) {
        FightPool<T> pool = new FightPool<>(type);
        return (FightPool<T>) Registries.POOL.register(new Identifier(Fight.getId(name)), pool);
    }
}

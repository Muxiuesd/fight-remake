package ttk.muxiuesd.pool;

import com.badlogic.gdx.utils.Pools;

/**
 * 游戏内用于注册的对象池
 */
public class FightPool<T> {
    private final Class<T> clazz;

    public FightPool (Class<T> clazz) {
        this.clazz = clazz;
        Pools.obtain(clazz);
    }

    public T obtain () {
        return Pools.obtain(this.clazz);
    }

    public void free (T object) {
        Pools.free(object);
    }
}

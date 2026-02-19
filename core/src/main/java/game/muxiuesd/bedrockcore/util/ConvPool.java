package game.muxiuesd.bedrockcore.util;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

/**
 * 方便对象池
 * <p>
 * 直接使用Gdx的池系统
 * */
public class ConvPool<T> {
    /// 这个池的对象类
    private final Class<T> clazz;

    public <P extends Pool<T>> ConvPool (Class<T> clazz, P pool) {
        this.clazz = clazz;
        Pools.set(clazz, pool);
    }

    public ConvPool (Class<T> clazz) {
        this.clazz = clazz;
        Pools.obtain(clazz);
    }

    /**
     * 获取对象
     * */
    public T obtain () {
        return Pools.obtain(this.clazz);
    }

    /**
     * 回收对象
     * */
    public void free (T object) {
        if (object == null) return;
        Pools.free(object);
    }

    /**
     * 获取空闲的对象的数量
     * */
    public int size () {
        return Pools.get(this.clazz).getFree();
    }
}

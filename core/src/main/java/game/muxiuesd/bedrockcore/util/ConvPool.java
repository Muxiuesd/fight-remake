package game.muxiuesd.bedrockcore.util;

import com.badlogic.gdx.utils.DefaultPool;
import com.badlogic.gdx.utils.Pool;

/**
 * 方便对象池
 * <p>
 * 直接使用Gdx的池系统（已适配新版）
 * */
public class ConvPool<T> {
    /// 这个池的对象类
    private final Class<T> clazz;

    public <P extends Pool<T>> ConvPool (Class<T> clazz, P pool) {
        this.clazz = clazz;
        PoolsManager.getInstance().addPool(pool);
    }

    public ConvPool (DefaultPool.PoolSupplier supplier) {
        Object object = supplier.get();
        this.clazz = (Class<T>) object.getClass();
        PoolsManager.getInstance().addPool(supplier);
    }

    /**
     * 获取对象
     * */
    public T obtain () {
        return PoolsManager.getInstance().obtain(this.clazz);
    }

    /**
     * 回收对象
     * */
    public void free (T object) {
        if (object == null) return;
        PoolsManager.getInstance().free(object);
    }

    /**
     * 获取空闲的对象的数量
     * */
    public int size () {
        return PoolsManager.getInstance().getPool(this.clazz).getFree();
    }

    /**
     * 获取这个对象池持有的对象类名称
     * */
    public Class<T> getClazz () {
        return this.clazz;
    }
}

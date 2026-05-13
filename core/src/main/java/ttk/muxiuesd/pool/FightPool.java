package ttk.muxiuesd.pool;

import com.badlogic.gdx.utils.DefaultPool;
import com.badlogic.gdx.utils.Pool;
import game.muxiuesd.bedrockcore.util.ConvPool;

/**
 * 游戏内用于注册的对象池
 */
public class FightPool<T> extends ConvPool<T> {

    public <P extends Pool<T>> FightPool (Class<T> clazz, P pool) {
        super(clazz, pool);
    }

    public <P extends Pool<T>> FightPool (DefaultPool.PoolSupplier supplier) {
        super(supplier);
    }
}

package ttk.muxiuesd.util.pool;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Pool;

/**
 * 池化的矩形
 * */
public class PoolableRectangle extends Rectangle implements Pool.Poolable {
    @Override
    public void reset () {
        set(0, 0, 0, 0);
    }
}

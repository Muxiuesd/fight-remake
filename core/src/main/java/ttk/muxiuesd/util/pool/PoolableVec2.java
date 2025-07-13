package ttk.muxiuesd.util.pool;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

/**
 * 可以池化的vec2
 * */
public class PoolableVec2 extends Vector2 implements Pool.Poolable {
    @Override
    public void reset () {
        set(0, 0);
    }
}

package ttk.muxiuesd.world.block;

import com.badlogic.gdx.math.Vector2;

/**
 * 方块位置
 * */
public class BlockPos extends Vector2 {
    public BlockPos () {
    }

    public BlockPos (float x, float y) {
        super(x, y);
    }

    public BlockPos (Vector2 v) {
        super(v);
    }
}

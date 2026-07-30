package ttk.muxiuesd.world.block;

import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.serialization.Codec;

import java.util.List;

/**
 * 方块位置
 * */
public class BlockPos extends Vector2 {
    public static final Codec<BlockPos> CODEC = Codec.listOf(Codec.FLOAT)
        .xmap(
            list -> new BlockPos(list.get(0), list.get(1)), // 解码
            vec -> List.of(vec.getX(), vec.getY())            // 编码
        );

    public BlockPos () {
    }

    public BlockPos (float x, float y) {
        super(x, y);
    }

    public BlockPos (Vector2 v) {
        super(v);
    }

    public float getX () {
        return x;
    }

    public BlockPos setX (float x) {
        this.x = x;
        return this;
    }

    public float getY () {
        return y;
    }

    public BlockPos setY (float y) {
        this.y = y;
        return this;
    }
}

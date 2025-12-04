package ttk.muxiuesd.interfaces.render.world.block;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Pool;
import ttk.muxiuesd.pool.FightPool;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.wall.Wall;

/**
 * 墙体的渲染器
 * */
public interface WallRenderer<T extends Wall<?>> {
    void render(Batch batch, T wall, WallRenderer.Context context);

    default WallRenderer.Context getContext () {
        return WallRenderer.Context.POOL.obtain();
    }

    default void freeContext (WallRenderer.Context context) {
        WallRenderer.Context.POOL.free(context);
    }

    /**
     * 渲染上下文，用于传递渲染信息
     * */
    class Context implements Pool.Poolable {
        //池化
        public static FightPool<WallRenderer.Context> POOL = new FightPool<>(WallRenderer.Context.class, new Pool<WallRenderer.Context>() {
            @Override
            protected WallRenderer.Context newObject () {
                return new WallRenderer.Context();
            }
        });

        public float
            x , y,
            width = Block.BlockWidth, height = Block.BlockHeight,
            originX = 0f, originY = 0f,
            scaleX = 1f, scaleY = 1f,
            rotation = 0f;

        public Context() {}
        public Context (float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public void reset () {
            this.x = 0f;
            this.y = 0f;
            this.width = Block.BlockWidth;
            this.height = Block.BlockHeight;
            this.originX = 0f;
            this.originY = 0f;
            this.scaleX = 1f;
            this.scaleY = 1f;
            this.rotation = 0f;
        }
    }

    /**
     * 普通标准的墙体渲染器
     * */
    class StandardRenderer<T extends Wall<?>> implements WallRenderer<T>{
        @Override
        public void render (Batch batch, T wall, Context context) {
            if (wall.textureIsValid()) {
                batch.draw(wall.getTextureRegion(),
                    context.x, context.y,
                    context.originX, context.originY,
                    context.width, context.height,
                    context.scaleX, context.scaleY,
                    context.rotation);
            }
        }
    }
}

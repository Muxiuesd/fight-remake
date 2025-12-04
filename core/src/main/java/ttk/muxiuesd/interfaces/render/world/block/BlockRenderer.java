package ttk.muxiuesd.interfaces.render.world.block;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Pool;
import ttk.muxiuesd.pool.FightPool;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 方块的渲染器接口
 * */
public interface BlockRenderer<T extends Block> {
    void render(Batch batch, T block, Context context);

    default Context getContext () {
        return Context.POOL.obtain();
    }

    default void freeContext (Context context) {
        Context.POOL.free(context);
    }

    /**
     * 渲染上下文，用于传递渲染信息
     * */
    class Context implements Pool.Poolable {
        //池化
        public static FightPool<Context> POOL = new FightPool<>(Context.class, new Pool<Context>() {
            @Override
            protected Context newObject () {
                return new Context();
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
     * 普通标准的方块渲染器
     * */
    class StandardRenderer<T extends Block> implements BlockRenderer<T>{
        @Override
        public void render (Batch batch, T block, Context context) {
            if (block.textureIsValid()) {
                batch.draw(block.getTextureRegion(),
                    context.x, context.y,
                    context.originX, context.originY,
                    context.width, context.height,
                    context.scaleX, context.scaleY,
                    context.rotation);
            }
        }
    }
}

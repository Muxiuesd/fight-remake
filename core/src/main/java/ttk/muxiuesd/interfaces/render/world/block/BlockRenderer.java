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
        return Context.pool.obtain();
    }

    default void freeContext (Context context) {
        Context.pool.free(context);
    }

    /**
     * 渲染上下文，用于传递渲染信息
     * */
    class Context {
        //池化
        public static FightPool<Context> pool = new FightPool<>(Context.class, new Pool<Context>() {
            @Override
            protected Context newObject () {
                return new Context();
            }
        });

        public float
            x , y,
            width = Block.BlockWidth, height = Block.BlockHeight,
            originX = 0, originY = 0,
            scaleX = 1, scaleY = 1,
            rotation = 0;

        public Context() {}
        public Context (float x, float y) {
            this.x = x;
            this.y = y;
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

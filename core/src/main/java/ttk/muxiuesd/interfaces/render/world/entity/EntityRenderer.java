package ttk.muxiuesd.interfaces.render.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import ttk.muxiuesd.pool.FightPool;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 实体得渲染器接口
 * */
public interface EntityRenderer<T extends Entity<?>> {
    /**
     * 图像的绘制
     * */
    void draw (Batch batch, T entity, Context context);

    /**
     * 图形的绘制
     * */
    void drawShape (ShapeRenderer batch, T entity, Context context);


    default EntityRenderer.Context getContext () {
        return EntityRenderer.Context.POOL.obtain();
    }

    /**
     * 直接使用实体当前的状态来作为渲染上下文参数
     * */
    default EntityRenderer.Context getContext (T entity) {
        Vector2 position = entity.getPosition();
        Vector2 origin = entity.getOrigin();
        Vector2 scale = entity.getScale();
        return getContext(
            position.x, position.y,
            entity.getWidth(), entity.getHeight(),
            origin.x, origin.y,
            scale.x, scale.y,
            entity.getRotation()
        );
    }

    default EntityRenderer.Context getContext (float x, float y, float width, float height) {
        Context context = Context.POOL.obtain();
        context.x = x;
        context.y = y;
        context.width = width;
        context.height = height;
        return context;
    }

    /**
     * 单独设置每一项渲染上下文参数
     * */
    default EntityRenderer.Context getContext (float x, float y,
                                               float width, float height,
                                               float originX, float originY,
                                               float scaleX, float scaleY,
                                               float rotation) {
        Context context = Context.POOL.obtain();
        context.x = x;
        context.y = y;
        context.width = width;
        context.height = height;
        context.originX = originX;
        context.originY = originY;
        context.scaleX = scaleX;
        context.scaleY = scaleY;
        context.rotation = rotation;
        return context;
    }

    /**
     * 回收上下文参数类
     * */
    default void freeContext (EntityRenderer.Context context) {
        EntityRenderer.Context.POOL.free(context);
    }

    /**
     * 渲染上下文，用于传递渲染信息
     * */
    class Context implements Pool.Poolable {
        //池化
        public static FightPool<EntityRenderer.Context> POOL
            = new FightPool<>(EntityRenderer.Context.class, new Pool<EntityRenderer.Context>() {
            @Override
            protected EntityRenderer.Context newObject () {
                return new EntityRenderer.Context();
            }
        });

        public float
            x , y,
            width, height,
            originX = 0f, originY = 0f,
            scaleX = 1f, scaleY = 1f,
            rotation = 0f;

        public Context() {}
        public Context (float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        @Override
        public void reset () {
            this.x = 0f;
            this.y = 0f;
            this.width = 1.145f;
            this.height = 1.145f;
            this.originX = 0f;
            this.originY = 0f;
            this.scaleX = 1f;
            this.scaleY = 1f;
            this.rotation = 0f;
        }
    }
}

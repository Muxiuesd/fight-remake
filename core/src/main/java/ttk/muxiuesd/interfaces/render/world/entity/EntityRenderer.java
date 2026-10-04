package ttk.muxiuesd.interfaces.render.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.pool.FightPool;
import ttk.muxiuesd.resource.Resource;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 实体的渲染器接口
 * <p>
 * 实体的贴图资源由渲染器持有，实体本身不持有贴图
 * */
public interface EntityRenderer<T extends Entity<?>> {
    /**
     * 获取实体的贴图
     * <p>
     * 默认返回null，没有贴图的渲染器（如自定义渲染器）可以不实现
     * */
    default TextureRegion getTextureRegion () {
        return null;
    }

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

        /**
         * 是否水平镜像绘制
         * <p>
         * 与几何参数无关：x/y/width/height/origin/scale/rotation 只决定四边形的位置与形状，
         * 而绘制时写进顶点的 UV 是直接取自贴图对象的（{@code SpriteBatch.draw} 里
         * {@code u = region.u; u2 = region.u2;}，没有任何跟缩放相关的分支），
         * 所以镜像只能靠换一张 UV 相反的贴图，这个字段就是"要不要换"的信号
         * */
        public boolean flipX = false;

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
            this.flipX = false;
        }
    }

    /**
     * 标准实体渲染器
     * <p>
     * 持有实体的贴图资源
     * */
    class StandardRenderer<T extends Entity<?>> implements EntityRenderer<T> {
        private final Resource<TextureRegion> textureRegionResource;
        /**
         * 水平镜像的贴图资源
         * <p>
         * 为 null 表示这个渲染器没有镜像贴图，此时即使上下文要求镜像也只会画原贴图
         * */
        private final Resource<TextureRegion> flippedTextureRegionResource;

        /**
         * @param textureId 贴图资源的id（一般与实体id相同）
         * @param texturePath 贴图文件在 texture/entity 目录下的路径
         * */
        public StandardRenderer (String textureId, String texturePath) {
            this(Resource.ofTextureRegion(textureId, Fight.EntityTexturePath(texturePath)), null);
        }

        /**
         * 带镜像贴图的构造
         *
         * @param textureRegionResource 贴图资源
         * @param flippedTextureRegionResource 水平镜像的贴图资源，
         *                                     由 {@link Resource#ofFlippedTextureRegion} 创建，
         *                                     没有则传 null
         * */
        public StandardRenderer (Resource<TextureRegion> textureRegionResource,
                                 Resource<TextureRegion> flippedTextureRegionResource) {
            this.textureRegionResource = textureRegionResource;
            this.flippedTextureRegionResource = flippedTextureRegionResource;
        }

        @Override
        public TextureRegion getTextureRegion () {
            return this.textureRegionResource.get();
        }

        /**
         * 获取水平镜像的贴图（首次调用时才真正创建）
         * <p>
         * 没有配置镜像贴图时返回 null，调用方需要判空
         * */
        public TextureRegion getFlippedTextureRegion () {
            if (this.flippedTextureRegionResource == null) return null;
            return this.flippedTextureRegionResource.get();
        }

        @Override
        public void draw (Batch batch, T entity, Context context) {
            //最基础的绘制，绘制实体的身体贴图
            //需要镜像绘制的时候用镜像贴图，否则用原贴图
            TextureRegion bodyTextureRegion = context.flipX ? this.getFlippedTextureRegion() : this.getTextureRegion();
            if (bodyTextureRegion != null) {
                batch.draw(bodyTextureRegion,
                    context.x - context.width / 2, context.y - context.height / 2,
                    context.originX, context.originY,
                    context.width, context.height,
                    context.scaleX, context.scaleY,
                    context.rotation
                );
            }
        }

        @Override
        public void drawShape (ShapeRenderer batch, T entity, Context context) {
        }
    }
}

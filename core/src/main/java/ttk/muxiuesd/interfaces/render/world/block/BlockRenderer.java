package ttk.muxiuesd.interfaces.render.world.block;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;
import ttk.muxiuesd.pool.FightPool;
import ttk.muxiuesd.resource.Resource;
import ttk.muxiuesd.world.block.abs.Block;

/**
 * 方块的渲染器接口
 * <p>
 * 方块的贴图资源由渲染器持有
 * */
public interface BlockRenderer<T extends Block> {
    /**
     * 获取方块的贴图
     * <p>
     * 默认返回null，没有贴图的渲染器（如自定义渲染器）可以不实现
     * */
    default TextureRegion getTextureRegion () {
        return null;
    }

    /**
     * 待实现的渲染方法
     * */
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
            width = Block.WIDTH, height = Block.HEIGHT,
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
            this.width = Block.WIDTH;
            this.height = Block.HEIGHT;
            this.originX = 0f;
            this.originY = 0f;
            this.scaleX = 1f;
            this.scaleY = 1f;
            this.rotation = 0f;
        }
    }

    /**
     * 普通标准的方块渲染器
     * <p>
     * 持有方块的贴图资源
     * */
    class StandardRenderer<T extends Block> implements BlockRenderer<T>{
        /// 渲染向左下角偏移半个方块距离
        public static final float OFFSET_X = - Block.WIDTH / 2;
        public static final float OFFSET_Y = - Block.HEIGHT / 2;

        private final Resource<TextureRegion> textureRegionResource;

        /**
         * @param textureId 贴图资源的id（一般与方块id相同）
         * @param texturePath 贴图文件的路径，为null时通过id从已注册的映射中获取
         * */
        public StandardRenderer (String textureId, String texturePath) {
            this.textureRegionResource = Resource.ofTextureRegion(textureId, texturePath);
        }

        @Override
        public TextureRegion getTextureRegion () {
            return this.textureRegionResource.get();
        }

        @Override
        public void render (Batch batch, T block, Context context) {
            TextureRegion textureRegion = getTextureRegion();
            if (textureRegion == null) return;
            batch.draw(textureRegion,
                context.x + OFFSET_X, context.y + OFFSET_Y,
                context.originX, context.originY,
                context.width, context.height,
                context.scaleX, context.scaleY,
                context.rotation);
        }
    }
}

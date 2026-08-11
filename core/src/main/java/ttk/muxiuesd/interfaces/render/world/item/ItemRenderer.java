package ttk.muxiuesd.interfaces.render.world.item;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import ttk.muxiuesd.interfaces.render.world.entity.EntityRenderer;
import ttk.muxiuesd.pool.FightPool;
import ttk.muxiuesd.resource.Resource;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 物品的渲染器
 * <p>
 * 物品的贴图资源由渲染器持有
 * */
public interface ItemRenderer<T extends Item> {
    /**
     * 获取物品的贴图
     * <p>
     * 默认返回null，没有贴图的渲染器（如自定义渲染器）可以不实现
     * */
    default TextureRegion getTextureRegion () {
        return null;
    }

    /**
     * 在持有者手上持有时的绘制方法
     * TODO 不同类型的物品不同的绘制方式
     * */
    void drawOnHand (Batch batch, Context context, LivingEntity<?> holder, ItemStack itemStack);

    /**
     * 在掉落物形式下的绘制方法
     * @param itemEntity 所属的物品实体
     * */
    void drawOnItemEntity (Batch batch, Context context, ItemEntity itemEntity);

    /**
     * 单纯的根据渲染上下文参数绘制物品
     * */
    default void draw (Batch batch, Context context, ItemStack itemStack) {
        if (itemStack == ItemStack.VOID) return;

        TextureRegion textureRegion = getTextureRegion();
        if (textureRegion == null) return;

        batch.draw(textureRegion,
            context.x, context.y,
            context.originX, context.originY,
            context.width, context.height,
            context.scaleX, context.scaleY,
            context.rotation);
    };

    /**
     * 在实体手持形式下的形状绘制
     * @param holder 所属的实体
     * @param itemStack 物品堆叠
     * */
    void renderShapeOnHand (ShapeRenderer batch, LivingEntity<?> holder, ItemStack itemStack);

    /**
     * 在掉落物形式下的形状绘制
     * @param itemEntity 所属的物品实体
     * */
    void renderShapeOnItemEntity (ShapeRenderer batch, ItemEntity itemEntity);

    /**
     * 直接使用物品实体当前的状态来作为渲染上下文参数
     * */
    default ItemRenderer.Context getContext (ItemEntity itemEntity) {
        return getContextByEntity(itemEntity);
    }

    /**
     * 直接使用实体当前的状态来作为渲染上下文参数
     * */
    default <E extends Entity<?>> ItemRenderer.Context getContextByEntity (E holder) {
        Vector2 position = holder.getPosition();
        Vector2 origin = holder.getOrigin();
        Vector2 scale = holder.getScale();
        return getContext(
            position.x, position.y,
            holder.getWidth(), holder.getHeight(),
            origin.x, origin.y,
            scale.x, scale.y,
            holder.getRotation()
        );
    }

    /**
     * 直接使用实体当前的渲染上下文参数来作为物品渲染上下文参数
     * */
    default <E extends Entity<?>> ItemRenderer.Context getContextByEntityContext (EntityRenderer.Context holderContext) {
        return getContext(
            holderContext.x, holderContext.y,
            holderContext.width, holderContext.height,
            holderContext.originX, holderContext.originY,
            holderContext.scaleX, holderContext.scaleY,
            holderContext.rotation
        );
    }

    default ItemRenderer.Context getContext (float x, float y, float width, float height) {
        ItemRenderer.Context context = getContext();
        context.x = x;
        context.y = y;
        context.width = width;
        context.height = height;
        return context;
    }

    /**
     * 单独设置每一项渲染上下文参数
     * */
    default ItemRenderer.Context getContext (float x, float y,
                                               float width, float height,
                                               float originX, float originY,
                                               float scaleX, float scaleY,
                                               float rotation) {
        ItemRenderer.Context context = getContext();
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

    default ItemRenderer.Context getContext () {
        return ItemRenderer.Context.POOL.obtain();
    }

    /**
     * 回收上下文参数类，每次渲染完后记得调用此类回收
     * */
    default void freeContext (ItemRenderer.Context context) {
        ItemRenderer.Context.POOL.free(context);
    }


    /**
     * 渲染上下文，用于传递渲染信息
     * */
    class Context implements Pool.Poolable {
        //池化
        public static FightPool<ItemRenderer.Context> POOL
            = new FightPool<>(ItemRenderer.Context.class, new Pool<ItemRenderer.Context>() {
            @Override
            protected ItemRenderer.Context newObject () {
                return new ItemRenderer.Context();
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

    /**
     * 普通标准的物品渲染器
     * <p>
     * 持有物品的贴图资源
     * */
    class StandardRenderer<T extends Item> implements ItemRenderer<T> {
        private final Resource<TextureRegion> textureRegionResource;

        /**
         * @param textureId 贴图资源的id（一般与物品id相同，方块物品为方块的id）
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
        public void drawOnHand (Batch batch, Context context, LivingEntity<?> holder, ItemStack itemStack) {
            Direction direction = holder.getDirection();
            float rotation = MathUtils.atan2Deg360(direction.getY(), direction.getX());
            float rotationOffset = holder.getSwingHandDegreeOffset();

            if (rotation > 90f && rotation <= 270f) {
                batch.draw(getTextureRegion(),
                    context.x - context.width / 2, context.y - context.height / 2,
                    context.originX, context.originY,
                    context.width, context.height,
                    - context.scaleX, context.scaleY,
                    context.rotation + 225f + rotationOffset);
            } else {
                batch.draw(getTextureRegion(),
                    context.x - context.width / 2, context.y - context.height / 2,
                    context.originX, context.originY,
                    context.width, context.height,
                    context.scaleX, context.scaleY,
                    context.rotation - 45f + rotationOffset);
            }
        }

        @Override
        public void drawOnItemEntity (Batch batch, Context context, ItemEntity itemEntity) {
            batch.draw(getTextureRegion(),
                context.x, context.y + itemEntity.getPositionOffset().y,
                context.originX, context.originY,
                context.width, context.height,
                context.scaleX, context.scaleY,
                context.rotation);
        }

        @Override
        public void renderShapeOnHand (ShapeRenderer batch, LivingEntity<?> holder, ItemStack itemStack) {

        }

        @Override
        public void renderShapeOnItemEntity (ShapeRenderer batch, ItemEntity itemEntity) {

        }
    }
}

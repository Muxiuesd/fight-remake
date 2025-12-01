package ttk.muxiuesd.interfaces.render.world.item;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import ttk.muxiuesd.pool.FightPool;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 物品的渲染器
 * */
public interface ItemRenderer<T extends Item> {
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
     * 在掉落物形式下的形状绘制
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
     * 回收上下文参数类
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
     * */
    class StandardRenderer<T extends Item> implements ItemRenderer<T> {

        @Override
        public void drawOnHand (Batch batch, Context context, LivingEntity<?> holder, ItemStack itemStack) {
            Item item = itemStack.getItem();
            Direction direction = holder.getDirection();
            float rotation = MathUtils.atan2Deg360(direction.getyDirection(), direction.getxDirection());
            float rotationOffset = holder.getSwingHandDegreeOffset();

            //直接利用持有者的各种参数来渲染
            if (rotation > 90f && rotation <= 270f) {
                batch.draw(item.textureRegion,
                    context.x + holder.getWidth() / 2, context.y + holder.getHeight() / 2,
                    0, 0,
                    context.width, context.height,
                    - context.scaleX, context.scaleY,
                    rotation + 225f + rotationOffset);
            } else {
                batch.draw(item.textureRegion,
                    context.x + holder.getWidth() / 2, context.y + holder.getHeight() / 2,
                    0, 0,
                    context.width, context.height,
                    context.scaleX, context.scaleY,
                    rotation - 45f + rotationOffset);
            }
        }

        @Override
        public void drawOnItemEntity (Batch batch, Context context, ItemEntity itemEntity) {
            Item item = itemEntity.getItemStack().getItem();
            if (item.textureRegion != null) {
                batch.draw(item.textureRegion,
                    context.x, context.y + itemEntity.getPositionOffset().y,
                    context.originX, context.originY,
                    context.width, context.height,
                    context.scaleX, context.scaleY, context.rotation);
            }
        }

        @Override
        public void renderShapeOnHand (ShapeRenderer batch, LivingEntity<?> holder, ItemStack itemStack) {

        }

        @Override
        public void renderShapeOnItemEntity (ShapeRenderer batch, ItemEntity itemEntity) {

        }
    }
}

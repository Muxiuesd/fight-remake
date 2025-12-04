package ttk.muxiuesd.render.world.item;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.render.world.item.ItemRenderer;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.util.CurveDrawer;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.common.EntityFishingHook;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.consumption.ItemFishPole;

/**
 * 钓竿的渲染器
 * */
public class FishPoleRenderer implements ItemRenderer<ItemFishPole> {
    @Override
    public void drawOnHand (Batch batch, Context context, LivingEntity<?> holder, ItemStack itemStack) {
        ItemFishPole fishPole = (ItemFishPole) itemStack.getItem();

        if (! fishPole.onUsing(itemStack)) {
            //if (fishPole.textureRegion == null) return;
            //没抛竿渲染
            Direction direction = holder.getDirection();
            float rotation = MathUtils.atan2Deg360(direction.getyDirection(), direction.getxDirection());
            if (rotation > 90f && rotation <= 270f) {
                batch.draw(fishPole.textureRegion,
                    holder.x + holder.getWidth() / 2, holder.y + holder.getHeight() / 2,
                    0, 0,
                    holder.width, holder.height,
                    - holder.scaleX, holder.scaleY, rotation + 180);
            } else {
                batch.draw(fishPole.textureRegion,
                    holder.x + holder.getWidth() / 2, holder.y + holder.getHeight() / 2,
                    0, 0,
                    holder.width, holder.height,
                    holder.scaleX, holder.scaleY, rotation);
            }
        }else {
            //if (fishPole.castTexture == null) return;
            //抛竿渲染
            Direction direction = holder.getDirection();
            float rotation = MathUtils.atan2Deg360(direction.getyDirection(), direction.getxDirection());
            if (rotation > 90f && rotation <= 270f) {
                batch.draw(fishPole.castTexture,
                    holder.x + holder.getWidth() / 2, holder.y + holder.getHeight() / 2,
                    0, 0,
                    holder.width, holder.height,
                    - holder.scaleX, holder.scaleY, rotation + 225f);
            } else {
                batch.draw(fishPole.castTexture,
                    holder.x + holder.getWidth() / 2, holder.y + holder.getHeight() / 2,
                    0, 0,
                    holder.width, holder.height,
                    holder.scaleX, holder.scaleY, rotation - 45f);
            }
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
                context.scaleX, context.scaleY,
                context.rotation
            );
        }
    }

    @Override
    public void renderShapeOnHand (ShapeRenderer batch, LivingEntity<?> holder, ItemStack itemStack) {
        ItemFishPole fishPole = (ItemFishPole) itemStack.getItem();
        if (!fishPole.onUsing(itemStack)) return;

        EntityFishingHook hook = (EntityFishingHook) itemStack.getProperty().get(PropertyTypes.ITEM_WITH_ENTITY);
        Direction direction = Util.getDirection();
        float rotation = MathUtils.atan2Deg360(direction.getyDirection(), direction.getxDirection());
        //绘制鱼线
        LivingEntity<?> hookOwner = hook.getOwner();
        Vector2 ownerPos = hookOwner.getCenter();
        float xOffset = hookOwner.getWidth() * 1.314f * MathUtils.cosDeg(rotation);
        float yOffset = hookOwner.getHeight()* 1.314f * MathUtils.sinDeg(rotation);
        ownerPos.add(xOffset, yOffset);

        Vector2 hookPos = hook.getCenter();
        //让鱼线绘制在钩子上方
        hookPos.add(0, hook.getHeight() / 2 - 0.07f + hook.getPositionOffset().y);
        //控制鱼线绘制方向
        if (ownerPos.x <= hookPos.x) CurveDrawer.drawCurve(batch, ownerPos, hookPos, -0.5f);
        else CurveDrawer.drawCurve(batch, hookPos, ownerPos, -0.5f);
    }

    @Override
    public void renderShapeOnItemEntity (ShapeRenderer batch, ItemEntity itemEntity) {

    }
}

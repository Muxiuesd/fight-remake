package ttk.muxiuesd.render.world.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.render.world.item.ItemRenderer;
import ttk.muxiuesd.registrant.ItemRendererRegistry;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 活物实体的渲染器
 * */
public class LivingEntityRenderer<T extends LivingEntity<?>> extends StandardEntityRenderer<T> {
    public static final Color ATTACTED_COLOR = new Color(1f, 0f, 0f, 1f);


    @Override
    public void draw (Batch batch, T entity, Context context) {
        //身体渲染
        if (!entity.isAttacked()) {
            super.draw(batch, entity, context);
        }else {
            // 受到攻击变红
            batch.setColor(ATTACTED_COLOR);
            super.draw(batch, entity, context);
            // 还原batch
            batch.setColor(1f, 1f, 1f, 1f);
        }

        //手持物品渲染
        if (entity.renderHandItem) {
            this.drawHandItem(batch, entity, context);
        }
    }

    /**
     * 手上持有的物品绘制
     * */
    public void drawHandItem (Batch batch, T entity, Context context) {
        //如果手上有物品，则绘制手上的物品
        ItemStack itemStack = entity.getHandItemStack();
        if (itemStack != null) {
            //获取物品的渲染器来渲染
            ItemRenderer<Item> renderer = ItemRendererRegistry.get(itemStack.getItem());
            if (renderer == null) return;

            ItemRenderer.Context itemContext = renderer.getContextByEntityContext(context);
            //物品渲染起点基于实体中心
            itemContext.x += context.width / 2;
            itemContext.y += context.height / 2;
            Vector2 mousePos = Util.getMouseWindowPos();
            itemContext.rotation = MathUtils.atan2Deg360(mousePos.y - itemContext.y, mousePos.x - itemContext.x);
            renderer.drawOnHand(batch, itemContext, entity, itemStack);
            renderer.freeContext(itemContext);
        }
    }


    @Override
    public void drawShape (ShapeRenderer batch, T entity, Context context) {
        if (entity.renderHandItem) this.renderShapeHandItem(batch, entity);
    }

    /**
     * 持有物品的形状渲染
     * */
    public void renderShapeHandItem (ShapeRenderer batch, T entity) {
        ItemStack itemStack = entity.getHandItemStack();
        if (itemStack != null) {
            //获取物品的渲染器来渲染
            ItemRenderer<Item> renderer = ItemRendererRegistry.get(itemStack.getItem());
            if (renderer == null) return;
            renderer.renderShapeOnHand(batch, entity, itemStack);
        }
    }
}

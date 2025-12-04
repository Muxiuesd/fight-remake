package ttk.muxiuesd.render.world.entity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

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
        if (entity.renderHandItem) this.drawHandItem(batch, entity, context);
    }

    /**
     * 手上持有的物品绘制
     * */
    public void drawHandItem (Batch batch, T entity, Context context) {
        //如果手上有物品，则绘制手上的物品
        ItemStack itemStack = entity.getHandItemStack();
        if (itemStack != null) {
            itemStack.drawItemOnHand(batch, entity);
        }
    }


    @Override
    public void drawShape (ShapeRenderer batch, T entity, Context context) {
        if (entity.renderHandItem) this.renderShapeHandItem(batch, entity, context);
    }

    /**
     * 持有物品的形状渲染
     * */
    public void renderShapeHandItem (ShapeRenderer batch, T entity, Context context) {
        ItemStack itemStack = entity.getHandItemStack();
        if (itemStack != null) {
            itemStack.renderShapeOnHand(batch, entity);
        }
    }
}

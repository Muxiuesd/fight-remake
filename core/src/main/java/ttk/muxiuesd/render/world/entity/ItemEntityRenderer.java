package ttk.muxiuesd.render.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.interfaces.render.world.entity.EntityRenderer;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品实体的渲染器
 * */
public class ItemEntityRenderer implements EntityRenderer<ItemEntity> {
    @Override
    public void draw (Batch batch, ItemEntity entity, Context context) {
        ItemStack stack = entity.getItemStack();
        if (stack != null) stack.getItem().drawOnWorld(batch, entity);
    }

    @Override
    public void drawShape (ShapeRenderer batch, ItemEntity entity, Context context) {

    }
}

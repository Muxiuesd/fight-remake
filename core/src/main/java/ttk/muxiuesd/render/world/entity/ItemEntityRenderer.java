package ttk.muxiuesd.render.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.render.world.entity.EntityRenderer;
import ttk.muxiuesd.interfaces.render.world.item.ItemRenderer;
import ttk.muxiuesd.registrant.ItemRendererRegistry;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 物品实体的渲染器
 * */
public class ItemEntityRenderer implements EntityRenderer<ItemEntity> {
    @Override
    public void draw (Batch batch, ItemEntity entity, Context context) {
        // 渲染影子（不随物品浮动偏移而上下移动，影子始终在地面）
        batch.setColor(1f, 1f, 1f, 0.666f);
        batch.draw(LivingEntityRenderer.ENTITY_SHADOW_RESOURCE.get(),
            context.x - context.width / 2f,
            context.y - (context.height / 5f) - (context.height / 2f),
            context.originX, context.originY,
            context.width, context.height,
            context.scaleX, context.scaleY / 2f,
            context.rotation
        );
        batch.setColor(1f, 1f, 1f, 1f);

        // 渲染物品贴图（带浮动偏移）
        ItemStack stack = entity.getItemStack();
        if (stack != null) {
            Item item = stack.getItem();
            ItemRenderer<Item> renderer = ItemRendererRegistry.get(item);
            if (renderer == null) return;
            TextureRegion textureRegion = renderer.getTextureRegion();
            if (textureRegion != null) {
                Vector2 origin = entity.getOrigin();
                Vector2 scale = entity.getScale();
                batch.draw(textureRegion,
                    entity.getX() - entity.getWidth() / 2f,
                    entity.getY() - entity.getHeight() / 2f + entity.getPositionOffset().y,
                    origin.x, origin.y,
                    entity.getWidth(), entity.getHeight(),
                    scale.x, scale.y,
                    entity.getRotation()
                );
            }
        }
    }

    @Override
    public void drawShape (ShapeRenderer batch, ItemEntity entity, Context context) {

    }
}

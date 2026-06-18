package ttk.muxiuesd.render.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.render.world.entity.EntityRenderer;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 物品实体的渲染器
 * */
public class ItemEntityRenderer implements EntityRenderer<ItemEntity> {
    @Override
    public void draw (Batch batch, ItemEntity entity, Context context) {
        ItemStack stack = entity.getItemStack();
        if (stack != null) {
            Item item = stack.getItem();
            TextureRegion textureRegion = item.getTextureRegion();
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

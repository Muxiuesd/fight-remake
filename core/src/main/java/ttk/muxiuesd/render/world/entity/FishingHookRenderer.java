package ttk.muxiuesd.render.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.interfaces.render.world.entity.EntityRenderer;
import ttk.muxiuesd.world.entity.common.EntityFishingHook;

/**
 * 鱼钩实体的渲染器
 * */
public class FishingHookRenderer implements EntityRenderer<EntityFishingHook> {
    @Override
    public void draw (Batch batch, EntityFishingHook entity, Context context) {
        if (entity.textureRegion != null) {
            batch.draw(entity.textureRegion,
                context.x, context.y + entity.getPositionOffset().y,
                context.originX, context.originY,
                context.width, context.height,
                context.scaleX, context.scaleY,
                context.rotation);
        }
    }

    @Override
    public void drawShape (ShapeRenderer batch, EntityFishingHook entity, Context context) {

    }
}

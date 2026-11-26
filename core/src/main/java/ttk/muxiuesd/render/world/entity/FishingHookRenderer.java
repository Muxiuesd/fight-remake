package ttk.muxiuesd.render.world.entity;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.render.world.entity.EntityRenderer;
import ttk.muxiuesd.world.entity.common.EntityFishingHook;

/**
 * 鱼钩实体的渲染器
 * */
public class FishingHookRenderer extends EntityRenderer.StandardRenderer<EntityFishingHook> {
    public FishingHookRenderer () {
        super(Fight.ID("fishing_hook"), "fish/fishing_hook.png");
    }

    @Override
    public void draw (Batch batch, EntityFishingHook entity, Context context) {
        TextureRegion region = getTextureRegion();
        if (region != null) {
            batch.draw(region,
                context.x - context.width / 2f, context.y - context.height /2f + entity.getPositionOffset().y,
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

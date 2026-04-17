package ttk.muxiuesd.render.world.block;

import com.badlogic.gdx.graphics.g2d.Batch;
import ttk.muxiuesd.interfaces.render.world.block.BlockRenderer;
import ttk.muxiuesd.world.block.abs.Botany;

/**
 * 植物渲染器
 * */
public class BotanyRenderer<T extends Botany> extends BlockRenderer.StandardRenderer<T> {
    @Override
    public void render (Batch batch, T botany, Context context) {
        if (botany.textureIsValid()) {
            batch.draw(
                botany.getCurGrowLevelTextureRegion(),
                context.x + OFFSET_X, context.y,
                context.originX, context.originY,
                context.width, context.height,
                context.scaleX, context.scaleY,
                context.rotation
            );
        }
    }
}
